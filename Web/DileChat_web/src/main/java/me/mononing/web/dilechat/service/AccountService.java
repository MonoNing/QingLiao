package me.mononing.web.dilechat.service;

import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.api.account.AccountRespModel;
import me.mononing.web.dilechat.bean.api.account.LoginModel;
import me.mononing.web.dilechat.bean.api.base.ResponseModel;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.bean.api.account.RegisterModel;
import me.mononing.web.dilechat.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/account")
public class AccountService extends BaseService{

    //GET 127.0.0.1/api/account/login
    @POST
    @Path("/login")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRespModel> login(LoginModel model) {

        if (!LoginModel.check(model)){
            //校验失败，返回参数异常
            return ResponseModel.buildParameterError();
        }

        //登录
        User user = UserFactory.login(
                model.getAccount().trim(),
                model.getPassword().trim()
        );
        if (user!=null){

            //如果存在设备id，进行绑定
            if (!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }

            //创建Result信息
            AccountRespModel result = new AccountRespModel(user);
            //返回登录成功信息
            return ResponseModel.buildOk(result);
        }else {
            //返回登录失败信息
           return ResponseModel.buildLoginError();
        }
    }


    //POST 127.0.0.1/api/account/login
    @POST
    @Path("/register")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRespModel> register(RegisterModel model) {

        if (!RegisterModel.check(model)){
            //校验失败，返回参数异常
            return ResponseModel.buildParameterError();
        }

        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user!=null){
            //返回已有账户错误信息
            return ResponseModel.buildHaveAccountError();
        }

        user = UserFactory.findByName(model.getName().trim());
        if (user!=null){
            //返回已有用户名错误
            return ResponseModel.buildHaveNameError();
        }
        user = UserFactory.register(model.getAccount()
                ,model.getPassword(),model.getName());
        if (user!=null){

            //如果存在设备id，进行绑定
            if (!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }
            //创建用户响应信息
            AccountRespModel result = new AccountRespModel(user);
            //返回注册成功信息
            return ResponseModel.buildOk(result);
        }else {
            //返回用户注册异常
            return ResponseModel.buildRegisterError();
        }
    }


    @POST
    @Path("/bind/{pushId}")
    // 指定请求与返回的相应体为JSON
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRespModel> register(@PathParam("pushId") String pushId){

        if (Strings.isNullOrEmpty(pushId)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        return bind(self, pushId);
    }


    /**
     * 绑定设备id
     * @param self 用户信息
     * @param pushId 设备Id
     * @return 返回绑定结果
     */
    private ResponseModel<AccountRespModel> bind(User self,String pushId) {
        User user = UserFactory.bindPushId(self, pushId);
        if (user == null) {
            //返回服务器异常
            return ResponseModel.buildServiceError();
        }

        AccountRespModel result = new AccountRespModel(user,true);
        return ResponseModel.buildOk(result);
    }
}
