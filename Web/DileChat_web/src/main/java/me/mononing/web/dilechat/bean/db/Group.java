package me.mononing.web.dilechat.bean.db;

import me.mononing.web.dilechat.bean.api.group.GroupCreateModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *@author Yaning
 *@data 2018/3/31 17:45
 *email 768305195@qq.com
 *
 * 群组Model
 */
@Entity
@Table(name = "TB_GROUP")
public class Group {

    @Id
    //主键不可更新，不可为空
    @PrimaryKeyJoinColumn
    @Column(updatable = false, nullable = false)
    //设置主键存储类型为UUID
    @GeneratedValue(generator = "uuid")
    //将UUID生成器修改为UUID2，UUID2是常规UUID的 toString
    @GenericGenerator(name = "uuid" ,strategy = "uuid2")
    private String id;

    //群名称
    @Column(nullable = false)
    private String name;

    //群图片，不可为空
    @Column(nullable = false)
    private String picture;

    //群的描述信息
    @Column
    private String description;

    //定义为创建时间戳，在用户创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime creatAt = LocalDateTime.now();

    //定义为更新时间戳，在用户创建时同时写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //群创建者实例
    //optional:创建者不能为空，所以是false
    //fetch：加载方式为急加载，意味着同时加载创建者信息
    //cascade：联级级别为ALL，所有的更改（更新，删除等）都将进行关系更新
    @ManyToOne(optional = false ,fetch = FetchType.EAGER ,cascade = CascadeType.ALL)
    @JoinColumn(name = "ownerId")
    private User owner;
    //群创建者ID，不为空，不可更新，不可插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String ownerId;

    public Group(User creator, GroupCreateModel model){
        this.owner =creator;
        this.name = model.getName();
        this.description = model.getDesc();
        this.picture = model.getPicture();
    }

    public Group(){
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
