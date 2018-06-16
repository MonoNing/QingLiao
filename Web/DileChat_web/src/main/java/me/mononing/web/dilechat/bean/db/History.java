package me.mononing.web.dilechat.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *@author Yaning
 *@data 2018/3/31 21:51
 *email 768305195@qq.com
 *
 * 消息推送历史记录Model
 */
@Entity
@Table(name = "TB_HISTORY")
public class History {

    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private String id;

    //推送消息都是json格式文本
    // BLOB 是比TEXT更多的一个大字段类型
    //推送实体
    @Lob
    @Column(nullable = false,columnDefinition = "BLOB")
    private String entity;

    //推送实体类型
    @Column(nullable = false,length = 11)
    private int entityType;

    //消息接受者
    //急加载方式，加载消息是同时加载用户消息
    //一个接受者可同时接受多条消息
    @ManyToOne(optional = false,fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "receivedId")
    private User receiver;
    //消息接受者Id
    @Column(nullable = false,updatable = false,insertable = false)
    private String receivedId;


    //一个用户可发送多条消息
    //可为空，可能是系统发送消息，急加载
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "senderId")
    private User sender;
    //消息发送者可为空，有可能是系统发送消息
    //不可插入，不可更新
    //发送者Id
    @Column(updatable = false,insertable = false)
    private String senderId;

    //接收者当前状态下的设备推送Id
    //可为空
    @Column
    private String receiverPushId;


    //定义为创建时间戳，在用户创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime creatAt = LocalDateTime.now();

    //定义为更新时间戳，在用户创建时同时写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //消息送达时间，可为空
    @Column
    private LocalDateTime arrivalAt = LocalDateTime.now();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceivedId() {
        return receivedId;
    }

    public void setReceivedId(String receivedId) {
        this.receivedId = receivedId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverPushId() {
        return receiverPushId;
    }

    public void setReceiverPushId(String receiverPushId) {
        this.receiverPushId = receiverPushId;
    }

    public LocalDateTime getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(LocalDateTime creatAt) {
        this.creatAt = creatAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(LocalDateTime arrivalAt) {
        this.arrivalAt = arrivalAt;
    }
}
