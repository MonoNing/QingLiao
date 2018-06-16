package me.mononing.web.dilechat.factory;

import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.api.group.GroupCreateModel;
import me.mononing.web.dilechat.bean.db.Group;
import me.mononing.web.dilechat.bean.db.GroupMember;
import me.mononing.web.dilechat.bean.db.Message;
import me.mononing.web.dilechat.bean.db.User;
import me.mononing.web.dilechat.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 群组的逻辑操作
 */
public class GroupFactory {

    // 通过Id来查找群
    public static Group findById(String id) {
        return Hib.query(session -> session.get(Group.class, id));
    }

    //查询一个群，同时这个人也属于这个群
    public static Group findById(User user, String groupId) {
        GroupMember member = getMember(user.getId(), groupId);
        if (member != null) {
            return member.getGroup();
        }
        return null;
    }

    //根据群名称查出群
    public static Group findByName(String name) {
        return Hib.query(session -> (Group) session
                .createQuery("from Group where lower(name)=:name")
                .setParameter("name", name.toLowerCase())
                .uniqueResult());
    }

    //获取一个群的所有成员
    public static Set<GroupMember> getMembers(Group group) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where group=:group")
                    .setParameter("group", group)
                    .list();

            return new HashSet<>(members);
        });
    }


    // 获取一个人加入的所有群
    public static Set<GroupMember> getMembers(User user) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where userId=:userId")
                    .setParameter("userId", user.getId())
                    .list();

            return new HashSet<>(members);
        });
    }

    //创建一个新群
    public static Group create(User creator, GroupCreateModel model, List<User> users) {
        return Hib.query(session -> {
            Group group = new Group(creator, model);
            //保存数据，但没有保存到数据库中，只是保存到缓存中
            session.save(group);

            //创建群管理员
            GroupMember ownerMember = new GroupMember(creator, group);
            //设置管理员权限
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            //保存数据，但没有保存到数据库中，只是保存到缓存中
            session.save(ownerMember);

            for (User user : users) {
                //创建相应的普通群成员
                GroupMember member = new GroupMember(user, group);
                //存数据到缓存中
                session.save(member);
            }

            return group;
        });
    }

    //查询出某一个群中的某一个群成员
    public static GroupMember getMember(String userId, String groupId) {
        return Hib.query(session -> (GroupMember) session
                .createQuery("from GroupMember where userId=:userId and groupId=:groupId")
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .setMaxResults(1)
                .uniqueResult()
        );
    }

    @SuppressWarnings("unchecked")
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = ""; // 保证不能为null的情况，减少后面的一下判断和额外的错误
        final String searchName = "%" + name + "%"; // 模糊匹配


        return Hib.query(session -> session
                .createQuery("from Group where lower(name) like :name")
                .setParameter("name", searchName)
                .setMaxResults(20)
                .list());
    }

    // 给群添加成员
    public static Set<GroupMember> addMembers(Group group, List<User> insertUsers) {
        return Hib.query(session -> {

            Set<GroupMember> members = new HashSet<>();

            for (User user : insertUsers) {
                GroupMember member = new GroupMember(user, group);
                // 保存，并没有提交到数据库
                session.save(member);
                members.add(member);
            }

            // 进行数据刷新
            /*
            for (GroupMember member : members) {
                // 进行刷新，会进行关联查询；再循环中消耗较高
                session.refresh(member);
            }
            */

            return members;
        });
    }
}
