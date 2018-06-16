package me.mononing.web.dilechat.factory;

import me.mononing.web.dilechat.bean.api.message.MessageCreateModel;
import me.mononing.web.dilechat.bean.db.Group;
import me.mononing.web.dilechat.bean.db.Message;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.utils.Hib;

/**
 * 消息实际逻辑处理类
 */
public class MessageFactory {

    //通过Id来查询消息
    public static Message findById(String id){
        //判断Id不为空
        if (id==null){
            return null;
        }

        return Hib.query(session -> session.get(Message.class,id));
    }

    /**
     * 添加普通消息
     * @param sender 发送者
     * @param receiver 接收者
     * @param model 发送消息
     * @return 返回已添加的消息
     */
    public static Message add(User sender, User receiver, MessageCreateModel model){
        Message message = new Message(sender,receiver,model);
        return save(message);
    }

    /**
     * 添加群组消息
     * @param sender 发送者
     * @param group 接收者
     * @param model 发送的消息
     * @return 返回已上传的消息
     */
    public static Message add(User sender, Group group, MessageCreateModel model){
        Message message = new Message(sender,group,model);
        return save(message);
    }
    //存储发送的消息
    public static Message save(Message message){
        return Hib.query(session -> {
            session.save(message);
            //写入数据库
            session.flush();
            //再从数据库中取出来，保证数据的最新
            session.refresh(message);
            //返回已存储消息
            return message;
        });
    }


}
