package me.mononing.web.dilechat.provider;


import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.api.base.ResponseModel;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

/**
 * 作用于全局的所有请求的接口的过滤和拦截
 *@author Yaning
 *@data 2018/4/14 11:56
 *email 768305195@qq.com
 */
public class AuthRequestFilter implements ContainerRequestFilter{

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //获取请求连接
        String relationPath = ((ContainerRequest)requestContext).getPath(false);
        if (relationPath.startsWith("account/login")
                ||relationPath.startsWith("account/register")){
            //如果是登录注册，那么就直接执行service
            return;
        }
        //获取头参数中的Token
        String token = requestContext.getHeaders().getFirst("token");
        //判断Token是否为空
        if (!Strings.isNullOrEmpty(token)){
            //通过Token获取用户数据
            final User user = UserFactory.findByToken(token);
            if (user!=null){
                //给当前请求设置一个上下文SecurityContext
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        //返回用户信息
                        return user;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        // 可以在这里写入用户的权限，role 是权限名，
                        // 可以管理管理员权限等等
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //URL安全，默认为false,HTTPS
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        //无须理会
                        return null;
                    }
                });
                //设置好上下文后，返回就好
                return;
            }
        }
        // 直接返回一个账户需要登录的Model
        ResponseModel model = ResponseModel.buildAccountError();
        //构建一个返回
        Response response = Response.status(Response.Status.OK)
                .entity(model)
                .build();
        //阻断下发到Service
        requestContext.abortWith(response);
    }
}
