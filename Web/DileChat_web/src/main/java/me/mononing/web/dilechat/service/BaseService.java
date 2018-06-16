package me.mononing.web.dilechat.service;

import me.mononing.web.dilechat.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * BaseService
 *@author Yaning
 *@data 2018/4/14 12:19
 *email 768305195@qq.com
 */
public class BaseService {

    @Context
    protected SecurityContext context;


    protected User getSelf(){
        return (User) context.getUserPrincipal();
    }

}
