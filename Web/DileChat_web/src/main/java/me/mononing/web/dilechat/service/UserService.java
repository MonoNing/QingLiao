package me.mononing.web.dilechat.service;

import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.api.base.PushModel;
import me.mononing.web.dilechat.bean.api.base.ResponseModel;
import me.mononing.web.dilechat.bean.card.UserCard;
import me.mononing.web.dilechat.bean.api.user.UpdateInfoModel;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.factory.PushFactory;
import me.mononing.web.dilechat.factory.UserFactory;
import me.mononing.web.dilechat.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息更新操作
 *@author Yaning
 *@data 2018/4/14 11:15
 *email 768305195@qq.com
 */
@Path("/user")
public class UserService extends BaseService{

    @PUT
    //@Path("") //127.0.0.1/api/user 不需要写，就是当前目录
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(
            @HeaderParam("token") String token, UpdateInfoModel model){
        //检查参数是否异常
        if (!UpdateInfoModel.check(model)){
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();

        self = model.updateToUser(self);
        self = UserFactory.update(self);
        if (self != null) {
            UserCard card = new UserCard(self, true);
            return ResponseModel.buildOk(card);
        } else {
            return ResponseModel.buildServiceError();
        }

    }

    /**
     * 获取联系人信息
     * @return response信息
     */
    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact(){
        User self = getSelf();

        //获取联系人信息
        List<User> users = UserFactory.contact(self);
        //将联系人转化成UserCard
        List<UserCard> userCards = users.stream()
                .map(user -> new UserCard(user,true))
                .collect(Collectors.toList());
        //返回信息
        return ResponseModel.buildOk(userCards);
    }

    /**
     * 关注人操作
     * 双方同时关注
     * @param followId 被关注用户Id
     * @return 返回响应信息
     */
    @PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId){
        //获取当前用户信息
        User self = getSelf();
        //如果关注人为自己的话则不能关注
        if (self.getId().equalsIgnoreCase(followId)
                ||Strings.isNullOrEmpty(followId)){
            //返回参数错误
            return ResponseModel.buildParameterError();
        }
        //通过联系人Id找到被关注人信息
        User userFollow = UserFactory.findById(followId);
        if (userFollow==null){
            //返回用户未找到错误信息
            return ResponseModel.buildNotFoundUserError(null);
        }
        //默认没有备注信息
        userFollow = UserFactory.follow(self,userFollow,null);

        // 通知我关注的人我关注他
        // 给他发送一个我的信息过去
        PushFactory.pushFollow(userFollow, new UserCard(self));

        return ResponseModel.buildOk(new UserCard(userFollow,true));
    }


    // 获取某人的信息
    @GET
    @Path("{id}") // http://127.0.0.1/api/user/{id}
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            // 返回参数异常
            return ResponseModel.buildParameterError();
        }


        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            // 返回自己，不必查询数据库
            return ResponseModel.buildOk(new UserCard(self, true));
        }


        User user = UserFactory.findById(id);
        if (user == null) {
            // 没找到，返回没找到用户
            return ResponseModel.buildNotFoundUserError(null);
        }


        // 如果我们直接有关注的记录，则我已关注需要查询信息的用户
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }


    // 搜索人的接口实现
    // 为了简化分页：只返回20条数据
    @GET // 搜索人，不涉及数据更改，只是查询，则为GET
    // http://127.0.0.1/api/user/search/
    @Path("/search/{name:(.*)?}") // 名字为任意字符，可以为空
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();

        // 先查询数据
        List<User> searchUsers = UserFactory.search(name);
        // 把查询的人封装为UserCard
        // 判断这些人是否有我已经关注的人，
        // 如果有，则返回的关注状态中应该已经设置好状态

        // 拿出我的联系人
        final List<User> contacts = UserFactory.contact(self);

        // 把User->UserCard
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    // 判断这个人是否是我自己，或者是我的联系人中的人
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            // 进行联系人的任意匹配，匹配其中的Id字段
                            || contacts.stream().anyMatch(
                            contactUser -> contactUser.getId()
                                    .equalsIgnoreCase(user.getId())
                    );

                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        // 返回
        return ResponseModel.buildOk(userCards);
    }
}
