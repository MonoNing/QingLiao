package me.mononing.web.dilechat.bean.db;


import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户model，对应数据库
 *
 * @author Yaning
 */
@Entity
@Table(name = "TB_USER")
public class User implements Principal {

    @Id
    //主键不可更新，不可为空
    @PrimaryKeyJoinColumn
    @Column(updatable = false, nullable = false)
    //设置主键存储类型为UUID
    @GeneratedValue(generator = "uuid")
    //将UUID生成器修改为UUID2，UUID2是常规UUID的 toString
    @GenericGenerator(name = "uuid" ,strategy = "uuid2")
    private String id;

    //用户名不为空，且唯一
    @Column(nullable = false , length = 128 ,unique = true)
    private String name;

    //密码不为空
    @Column(nullable = false)
    private String password;

    //联系电话不为空且唯一
    @Column(nullable = false,length = 64,unique = true)
    private String phone;

    //头像允许为空
    @Column
    private String portrait;

    //性别不为空，默认初始值为0
    @Column(nullable = false)
    private int sex = 0;

    //个人描述允许为空
    @Column
    private String description;

    //token用于拉去用户信息，所以token必须唯一
    @Column(unique = true)
    private String token;

    //与用户绑定的设备Id。用于推送消息
    @Column
    private String pushId;

    //定义为创建时间戳，在用户创建时就写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime creatAt = LocalDateTime.now();

    //定义为更新时间戳，在用户创建时同时写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //最后一次接受消息的时间
    @Column
    private LocalDateTime lastReceivedAt = LocalDateTime.now();


    // 我关注的人的列表方法
    // 对应的数据库表字段为TB_USER_FOLLOW.originId
    @JoinColumn(name = "originId")
    // 定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    // 1对多，一个用户可以有很多关注人，每一次关注都是一个记录
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollow> following = new HashSet<>();


    // 关注我的人的列表
    // 对应的数据库表字段为TB_USER_FOLLOW.targetId
    @JoinColumn(name = "targetId")
    // 定义为懒加载，默认加载User信息的时候，并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    // 1对多，一个用户可以被很多人关注，每一次关注都是一个记录
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollow> followers = new HashSet<>();

    // 我所有创建的群
    // 对应的字段为：Group.ownerId
    @JoinColumn(name = "ownerId")
    // 懒加载集合方式为尽可能的不加载具体的数据，
    // 当访问groups.size()仅仅查询数量，不加载具体的Group信息
    // 只有当遍历集合的时候才加载具体的数据
    @LazyCollection(LazyCollectionOption.EXTRA)
    // FetchType.LAZY：懒加载，加载用户信息时不加载这个集合
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Group> groups = new HashSet<>();


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
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

    public LocalDateTime getLastReceivedAt() {
        return lastReceivedAt;
    }

    public void setLastReceivedAt(LocalDateTime lastReceivedAt) {
        this.lastReceivedAt = lastReceivedAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserFollow> followers) {
        this.followers = followers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
