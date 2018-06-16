package me.mononing.web.dilechat.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import me.mononing.web.dilechat.bean.db.Message;

/**
 * 服务器接收消息的model
 */
public class MessageCreateModel {
    //Id
    @Expose
    private String id;
    @Expose
    private String attach;
    //内容
    @Expose
    private String content;
    //消息类型,默认为字符串
    @Expose
    private int type = Message.TYPE_STR;
    //对应消息发送者id
    //接受者Id
    @Expose
    private String receiverId;
    //接收方对应的类型,默认发送给个人的
    @Expose
    private int receivedType = Message.RECEIVER_TYPE_NONE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceivedId() {
        return receiverId;
    }

    public void setReceivedId(String receivedId) {
        this.receiverId = receivedId;
    }

    public int getReceivedType() {
        return receivedType;
    }

    public void setReceivedType(int receivedType) {
        this.receivedType = receivedType;
    }
    //判断上传的消息数据的合法性
    public static boolean check(MessageCreateModel model){
        //四个大条件中只要有一个不符合，就返回false结果，
        // 上传消息就为不合法，只有四个条件都满足才返回True

        return  model!=null

                &&!Strings.isNullOrEmpty(model.getId())
                ||Strings.isNullOrEmpty(model.getContent())
                ||Strings.isNullOrEmpty(model.getReceivedId())

                &&(model.receivedType == Message.RECEIVER_TYPE_NONE
                ||model.receivedType == Message.RECEIVER_TYPE_GROUP)

                &&(model.type == Message.TYPE_STR
                ||model.type == Message.TYPE_PIC
                ||model.type == Message.TYPE_AUDIO
                ||model.type == Message.TYPE_FILE);
    }
}
