package me.mononing.web.dilechat.service;

import me.mononing.web.dilechat.bean.api.base.PushModel;
import me.mononing.web.dilechat.bean.api.base.ResponseModel;
import me.mononing.web.dilechat.bean.api.message.MessageCreateModel;
import me.mononing.web.dilechat.bean.card.MessageCard;
import me.mononing.web.dilechat.bean.card.UserCard;
import me.mononing.web.dilechat.bean.db.Group;
import me.mononing.web.dilechat.bean.db.Message;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.factory.GroupFactory;
import me.mononing.web.dilechat.factory.MessageFactory;
import me.mononing.web.dilechat.factory.PushFactory;
import me.mononing.web.dilechat.factory.UserFactory;
import me.mononing.web.dilechat.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息处理Service
 */
@Path("/msg")
public class MessageService extends BaseService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        //判断上传数据的合法性，不合法返回参数异常
        if (!MessageCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //获取个人信息
        User self = getSelf();

        Message message = MessageFactory.findById(model.getId());
        if (message != null) {
            // 查询到已有消息直接返回
            return ResponseModel.buildOk(new MessageCard(message));
        }

        if (model.getReceivedType() == Message.RECEIVER_TYPE_GROUP) {
            return pushToGroup(self, model);
        } else {
            return pushToUser(self, model);
        }
    }
    //发送给普通用户
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.findById(model.getReceivedId());
        //接收者不能为空
        if (receiver == null){
            return ResponseModel.buildNotFoundUserError("未找到接收者用户");
        }

        //发送者不能与接收者为同一个人
        if (receiver.getId().equalsIgnoreCase(sender.getId())){
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //存储到数据库中
        Message message = MessageFactory.add(sender,receiver,model);

        return buildAndPushResponse(sender,message);
    }

    //发送给群组
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        //权限判断，判断该发送者是否在该群中
        Group group = GroupFactory.findById(sender,model.getReceivedId());
        //不在该群中，或者群被解散,直接返回错误
        if (group==null){
            return ResponseModel.buildNotFoundUserError("未能找到接收群组");
        }
        //存储到数据库中
        Message message = MessageFactory.add(sender,group,model);
        return buildAndPushResponse(sender,message);
    }
    //推送并构建一个返回信息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        //消息存储失败
        if (message==null){
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //推送消息
        PushFactory.pushNewMessage(sender,message);
        //返回响应结果
        return ResponseModel.buildOk(new MessageCard(message));
    }
}