package me.mononing.web.dilechat.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 申请记录表Model
 *
 *@author Yaning
 *@data 2018/3/31 22:37
 *email 768305195@qq.com
 */
@Entity
@Table(name = "TB_APPLY")
public class Apply {

    public static final int TYPE_ADD_USER = 1; // 添加好友
    public static final int TYPE_ADD_GROUP = 2; // 加入群

    @Id
    //主键不可更新，不可为空
    @PrimaryKeyJoinColumn
    @Column(updatable = false, nullable = false)
    //设置主键存储类型为UUID
    @GeneratedValue(generator = "uuid")
    //将UUID生成器修改为UUID2，UUID2是常规UUID的 toString
    @GenericGenerator(name = "uuid" ,strategy = "uuid2")
    private String id;

    //申请描述，比如我是XXX，请加我为好友吧（拉我进群吧）
    //不能为空
    @Column(nullable = false)
    private String description;

    //申请类型，可为添加好友，或者添加群
    @Column(nullable = false)
    private int type;

    //附件，可以作为图片地址
    //可为空
    @Column(columnDefinition = "TEXT")
    private String attach;

    //申请发送目标ID
    //type为人时，即为User。id
    //type为群时，即为Group。id
    @Column(nullable = false)
    private String targetId;

    // 申请人 可为空 为系统信息
    // 一个人可以有很多个申请
    @ManyToOne()
    @JoinColumn(name = "applicantId")
    private User applicant;
    //申请人ID
    @Column(updatable = false,insertable = false)
    private String applicantId;


    //定义为创建时间戳，在用户创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime creatAt = LocalDateTime.now();

    //定义为更新时间戳，在用户创建时同时写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
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
}
