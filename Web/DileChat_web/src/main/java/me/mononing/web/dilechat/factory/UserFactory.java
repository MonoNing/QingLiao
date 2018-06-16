package me.mononing.web.dilechat.factory;

import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.bean.db.UserFollow;
import me.mononing.web.dilechat.utils.Hib;
import me.mononing.web.dilechat.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户逻辑处理类
 *@author Yaning
 *@data 2018/4/13 15:42
 *email 768305195@qq.com
 */
public class UserFactory {
    /**
     * 通过手机号查询用户信息
     * @param token 用户token信息
     * @return 返回用户信息
     */
    public static User findByToken(String token){
        return Hib.query((Session session) ->
                (User) session.createQuery("from User where token=:inToken")
                        .setParameter("inToken", token)
                        .uniqueResult());
    }

    /**
     * 通过手机号查询用户信息
     * @param phone 手机号即账户
     * @return 如果查询到信息返回用户信息，否则返回空
     */
    public static User findByPhone(String phone){
        return Hib.query((Session session) ->
                (User) session.createQuery("from User where phone=:inPhone")
                        .setParameter("inPhone", phone)
                        .uniqueResult());
    }

    /**
     * 通过用户名查询用户信息
     * @param name 用户名
     * @return 如果查询到信息返回用户信息，否则返回空
     */
    public static User findByName(String name){
        return Hib.query((Session session) ->
                (User) session.createQuery("from User where name=:inName")
                        .setParameter("inName", name)
                        .uniqueResult());
    }

    /**
     * 通过用户Id查询用户信息
     * @param id 用户名
     * @return 如果查询到信息返回用户信息，否则返回空
     */
    public static User findById(String id){
        return Hib.query((Session session) ->session.get(User.class,id));
    }

    /**
     * 用户登录方法
     * @param account 账户
     * @param password 密码
     * @return 返回用户登录后信息
     */
    public static User login(String account,String password){
        //去掉账号首尾空格
        final String accountStr = account.trim();
        //对密码进行相同的加密
        final String encodePassword = encodePassword(password);
        //查询登录信息
        User user =  Hib.query(session -> (User) session.createQuery("from User where phone=:inAccount and password=:inPassword")
                .setParameter("inAccount", accountStr)
                .setParameter("inPassword",encodePassword)
                .uniqueResult());
        if (user!=null){
            //更新Token
            user = login(user);
        }
        //返回用户信息
        return user;
    }

    /**
     * 用户注册逻辑
     * @param account 账户信息
     * @param password 密码
     * @param name 用户名
     * @return 用户信息
     */
    public static User register(String account,String password,String name){
        //去掉用户首位空格
        account = account.trim();
        //密码加密
        password = encodePassword(password);
        //创建用户信息
        User user = createUser(account,password,name);
        if (user!=null){
            //更新Token
            user =  login(user);
        }
        //返回用户信息
        return user;
    }


    public static User bindPushId(User user,String pushId){
        //pushId为空时，直接返回空
        if (Strings.isNullOrEmpty(pushId)){
            return null;
        }

        Hib.queryOnly(session -> {
            @SuppressWarnings("unchecked")
            //查询拥有相同pushId的非当前用户
            List<User> userList= (List<User>) session
                    .createQuery("from User where pushId=:pushId and id!=:userId")
                    .setParameter("pushId",pushId.toLowerCase())
                    .setParameter("userId",user.getId())
                    .list();
            //将拥有相同pushId的非当前用户的pushId置空
            for (User u : userList){
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });

        if (pushId.equalsIgnoreCase(user.getPushId())){
            //用户当前设备与要绑定的设备一致时
            //不操作直接返回当前用户信息
            return user;
        }else {
            //当前登录设备与将要绑定设备不一致时
            // 那么需要单点登录，让之前的设备退出账户，
            // 给之前的设备推送一条退出消息
            if (!Strings.isNullOrEmpty(user.getPushId())){
                // 推送一个退出消息
                PushFactory.pushLogout(user, user.getPushId());
            }
            user.setPushId(pushId);
            return update(user);
        }

    }

    /**
     * 用户注册时创建用户
     * @param account 账户
     * @param password 密码
     * @param name 用户名
     * @return 返回用户信息
     */
    private static User createUser(String account,String password,String name){
        User user = new User();
        user.setPhone(account);
        user.setName(name);
        user.setPassword(password);

        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }

    /**
     * 用户登录，更新信息
     * @param user 用户信息
     * @return 返回用户登陆后信息
     */
    private static User login(User user){
        //生成随机UUID
        String newToken = UUID.randomUUID().toString();
        //进行Base64格式化
        newToken = TextUtil.encodeBase64(newToken);
        //添加用户Token
        user.setToken(newToken);
        //登录信息更新
        return update(user);
    }

    /**
     * 对密码进行加密
     * @param password 密码
     * @return 加密密码
     */
    private static String encodePassword(String password){
        //去掉首位空格
        password = password.trim();
        //密码进行MD5加密
        password = TextUtil.getMD5(password);
        //返回Base64加密，也可以进行加盐处理
        return TextUtil.encodeBase64(password);
    }

    /**
     * 更新用户信息到数据库
     * @param user user
     * @return user
     */
    public static User update(User user){
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 获取联系人列表
     * @param self user
     * @return 用户集合List<user></>
     */
    public static List<User> contact(User self){
        return Hib.query(session -> {
            session.load(self,self.getId());
            Set<UserFollow> follows = self.getFollowing();
            return follows.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());
        });
    }

    public static User follow(User origin,User target,String alias){
        UserFollow userFollow = getUserFollow(origin,target);
        if (userFollow!=null){
            //如果已经关注了返回被关注者信息
            return userFollow.getTarget();
        }

        return Hib.query(session -> {
            // 想要操作懒加载的数据，需要重新load一次
            session.load(origin, origin.getId());
            session.load(target, target.getId());

            // 我关注人的时候，同时他也关注我，
            // 所有需要添加两条UserFollow数据
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            // 备注是我对他的备注，他对我默认没有备注
            originFollow.setAlias(alias);

            // 发起者是他，我是被关注的人的记录
            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);

            // 保存数据库
            session.save(originFollow);
            session.save(targetFollow);

            return target;
        });
    }

    /**
     * 查询发起者与被关注着之间的关注状态
     * @param origin 发起者
     * @param target 被关注者
     * @return 查询关注具体信息
     */
    public static UserFollow getUserFollow(User origin,User target){
        return Hib.query(session -> (UserFollow) session
                .createQuery("from UserFollow where originId = :originId and targetId = :targetId")
                .setParameter("originId", origin.getId())
                .setParameter("targetId", target.getId())
                .setMaxResults(1)
                // 唯一查询返回
                .uniqueResult());
    }

    /**
     * 搜索联系人的实现
     *
     * @param name 查询的name，允许为空
     * @return 查询到的用户集合，如果name为空，则返回最近的用户
     */
    @SuppressWarnings("unchecked")
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = ""; // 保证不能为null的情况，减少后面的一下判断和额外的错误
        final String searchName = "%" + name + "%"; // 模糊匹配

        return Hib.query(session -> {
            // 查询的条件：name忽略大小写，并且使用like（模糊）查询；
            // 头像和描述必须完善才能查询到
            return (List<User>) session
                    .createQuery("from User where lower(name) like :name and portrait is not null and description is not null")
                    .setParameter("name", searchName)
                    .setMaxResults(20) // 至多20条
                    .list();

        });

    }
}
