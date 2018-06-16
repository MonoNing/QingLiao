package me.mononing.web.dilechat.bean.card;

import com.google.gson.annotations.Expose;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.utils.Hib;

import java.time.LocalDateTime;

/**
 * 调用接口后，返回设备的用户信息卡片
 *
 *@author Yaning
 *@data 2018/4/12 10:56
 *email 768305195@qq.com
 */
public class UserCard {
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String portrait;
    @Expose
    private int sex = 0;
    @Expose
    private String desc;
    @Expose
    //当前用户粉丝数量
    private int followed;
    @Expose
    //用户当前关注人数
    private int following;
    @Expose
    //我与当前用户的关系状态，是否关注了这个人
    private  boolean isFollow;
    @Expose
    //定义为用户最后一次更新时间
    private LocalDateTime modifyAt = LocalDateTime.now();

    public UserCard(User user){
        this(user,false);
    }

    public UserCard(User user,boolean isFollow){
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.portrait = user.getPortrait();
        this.sex = user.getSex();
        this.desc = user.getDescription();
        this.modifyAt = user.getUpdateAt();
        this.isFollow = isFollow;

        //this.followed = user.getFollowers().size();
        //现在不能用这种方法获取，懒加载会报错

        // user.getFollowers().size()
        // 懒加载会报错，因为没有Session
        Hib.queryOnly(session -> {
            // 重新加载一次用户信息
            session.load(user, user.getId());
            // 这个时候仅仅只是进行了数量查询，并没有查询整个集合
            // 要查询集合，必须在session存在情况下进行遍历
            // 或者使用Hibernate.initialize(user.getFollowers());
            followed = user.getFollowers().size();
            following = user.getFollowing().size();
        });
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollow(int follow) {
        this.followed = follow;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollowed(boolean followed) {
        isFollow = followed;
    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
        this.modifyAt = modifyAt;
    }
}
