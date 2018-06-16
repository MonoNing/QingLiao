package me.mononing.web.dilechat.factory;

import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.api.base.PushModel;
import me.mononing.web.dilechat.bean.card.GroupMemberCard;
import me.mononing.web.dilechat.bean.card.MessageCard;
import me.mononing.web.dilechat.bean.card.UserCard;
import me.mononing.web.dilechat.bean.db.*;
import me.mononing.web.dilechat.utils.Hib;
import me.mononing.web.dilechat.utils.PushDispatcher;
import me.mononing.web.dilechat.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息存储与发送的方法
 */
public class PushFactory {

    //发送一条消息，并在当前的发送历史记录中保存消息记录
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null) {
            return;
        }
        //构建返回客户端的消息卡片
        MessageCard card = new MessageCard(message);
        //将卡片消息转换成json
        String entity = TextUtil.toJson(card);

        //构建消息发送类
        PushDispatcher dispatcher = new PushDispatcher();

        if (message.getGroup() == null
                && Strings.isNullOrEmpty(message.getGroupId())) {
            //不为群组发送，只是发送给普通朋友

            User receiver = UserFactory.findById(message.getReceiverId());
            //接收者不能为空
            if (receiver == null) {
                return;
            }
            //构建消息记录
            History history = new History();
            //设置消息类型
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setReceiver(receiver);
            history.setEntity(entity);
            //设置接收者pushId
            history.setReceiverPushId(receiver.getPushId());

            //构建推送消息的真实Model
            PushModel pushModel = new PushModel();
            //添加推送消息
            pushModel.add(history.getEntityType(), history.getEntity());

            //添加消息发送队列
            dispatcher.add(receiver, pushModel);
            //存储消息到服务器
            Hib.queryOnly(session -> session.save(history));
        } else {
            //发送给群组
            Group group = message.getGroup();
            //可能因为延迟加载的问题导致群信息未加载进去
            if (group == null) {
                group = GroupFactory.findById(message.getGroupId());
            }
            //真的不存在群，直接返回
            if (group == null) {
                return;
            }
            //获取群成员
            Set<GroupMember> members = GroupFactory.getMembers(group);
            //不存在群成员时，直接返回
            if (members==null||members.size()==0){
                return;
            }

            // 过滤我自己
            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId()
                            .equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            //群中只存在我自己一个的话，直接返回
            if (members.size()==0){
                return;
            }

            // 一个历史记录列表
            List<History> histories = new ArrayList<>();

            addGroupMembersPushModel(dispatcher, // 推送的发送者
                    histories, // 数据库要存储的列表
                    members,    // 所有的成员
                    entity, // 要发送的数据
                    PushModel.ENTITY_TYPE_MESSAGE); // 发送的类型

            // 保存到数据库的操作
            Hib.queryOnly(session -> {
                for (History history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }
        //发送者进行真是提交
        dispatcher.submit();
    }

    /**
     * 给群成员构建一个消息，
     * 把消息存储到数据库的历史记录中，每个人，每条消息都是一个记录
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher,
                                                 List<History> histories,
                                                 Set<GroupMember> members,
                                                 String entity,
                                                 int entityTypeMessage) {
        for (GroupMember member : members) {
            // 无须通过Id再去找用户
            User receiver = member.getUser();
            if (receiver == null)
                return;

            // 历史记录表字段建立
            History history = new History();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);

            // 构建一个消息Model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());

            // 添加到发送者的数据集中
            dispatcher.add(receiver, pushModel);
        }
    }

    /**
     * 通知一些成员，被加入了XXX群
     *
     * @param members 被加入群的成员
     */
    public static void pushJoinGroup(Set<GroupMember> members) {
        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();

        // 一个历史记录列表
        List<History> histories = new ArrayList<>();

        for (GroupMember member : members) {
            User receiver = member.getUser();
            if (receiver == null)
                return;

            // 每个成员的信息卡片
            GroupMemberCard memberCard = new GroupMemberCard(member);
            String entity = TextUtil.toJson(memberCard);

            // 历史记录表字段建立
            History history = new History();
            // 你被添加到群的类型
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);

            // 构建一个消息Model
            PushModel pushModel = new PushModel()
                    .add(history.getEntityType(), history.getEntity());

            // 添加到发送者的数据集中
            dispatcher.add(receiver, pushModel);
            histories.add(history);
        }

        // 保存到数据库的操作
        Hib.queryOnly(session -> {
            for (History history : histories) {
                session.saveOrUpdate(history);
            }
        });

        // 提交发送
        dispatcher.submit();
    }


    /**
     * 通知老成员，有一系列新的成员加入到某个群
     *
     * @param oldMembers  老的成员
     * @param insertCards 新的成员的信息集合
     */
    public static void pushGroupMemberAdd(Set<GroupMember> oldMembers, List<GroupMemberCard> insertCards) {
        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();

        // 一个历史记录列表
        List<History> histories = new ArrayList<>();

        // 当前新增的用户的集合的Json字符串
        String entity = TextUtil.toJson(insertCards);

        // 进行循环添加，给oldMembers每一个老的用户构建一个消息，消息的内容为新增的用户的集合
        // 通知的类型是：群成员添加了的类型
        addGroupMembersPushModel(dispatcher, histories, oldMembers,
                entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);

        // 保存到数据库的操作
        Hib.queryOnly(session -> {
            for (History history : histories) {
                session.saveOrUpdate(history);
            }
        });

        // 提交发送
        dispatcher.submit();
    }

    /**
     * 推送账户退出消息
     *
     * @param receiver 接收者
     * @param pushId   这个时刻的接收者的设备Id
     */
    public static void pushLogout(User receiver, String pushId) {
        // 历史记录表字段建立
        History history = new History();
        // 你被添加到群的类型
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account logout!!!");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);
        // 保存到历史记录表
        Hib.queryOnly(session -> session.save(history));

        // 发送者
        PushDispatcher dispatcher = new PushDispatcher();
        // 具体推送的内容
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());

        // 添加并提交到第三方推送
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }

    /**
     * 给一个朋友推送我的信息过去
     * 类型是：我关注了他
     *
     * @param receiver 接收者
     * @param userCard 我的卡片信息
     */
    public static void pushFollow(User receiver, UserCard userCard) {
        // 一定是相互关注了
        userCard.setFollowed(true);
        String entity = TextUtil.toJson(userCard);

        // 历史记录表字段建立
        History history = new History();
        // 你被添加到群的类型
        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiver(receiver);
        history.setReceiverPushId(receiver.getPushId());
        // 保存到历史记录表
        Hib.queryOnly(session -> session.save(history));

        // 推送
        PushDispatcher dispatcher = new PushDispatcher();
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }
}
