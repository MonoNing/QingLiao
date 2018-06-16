package me.mononing.web.dilechat.bean.api.account;

import com.google.gson.annotations.Expose;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.bean.card.UserCard;

public class AccountRespModel {

    //用户卡片，包含用户信息
    @Expose
    private UserCard card;
    //用户Token信息
    @Expose
    private String token;
    //用户当前登录账号
    @Expose
    private String account;
    //当前用户是否绑定设备pushId
    @Expose
    private boolean isBind;

    public AccountRespModel(User user){
        //默认不绑定设备pushId
        this(user,false);
    }

    public AccountRespModel(User user,boolean isBind){
        this.card = new UserCard(user);
        this.account = user.getPhone();
        this.token = user.getToken();
        this.isBind = isBind;
    }

    public UserCard getCard() {
        return card;
    }

    public void setCard(UserCard card) {
        this.card = card;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

}
