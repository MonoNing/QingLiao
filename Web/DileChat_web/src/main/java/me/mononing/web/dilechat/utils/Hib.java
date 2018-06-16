package me.mononing.web.dilechat.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Created by qiujuer
 * on 2017/2/17.
 */
public class Hib {
    // 全局SessionFactory
    private static SessionFactory sessionFactory;

    static {
        // 静态初始化sessionFactory
        init();
    }

    private static void init() {
        // 从hibernate.cfg.xml文件初始化
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            // build 一个sessionFactory
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            // 错误则打印输出，并销毁
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    /**
     * 获取全局的SessionFactory
     *
     * @return SessionFactory
     */
    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }

    /**
     * 从SessionFactory中得到一个Session会话
     *
     * @return Session
     */
    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory
     */
    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * 只查询接口
     */
    public interface QueryOnly{
        void query(Session session);
    }

    /**
     * 带参数查询接口
     * @param <T>
     */
    public interface Query<T>{
        T query(Session session);
    }

    /**
     * 只查询方法
     * @param query
     */
    public static void queryOnly(QueryOnly query){
        //获取Session
        Session session = Hib.sessionFactory().openSession();
        //开启一个事物
        final Transaction transaction = session.beginTransaction();
        try{
            //通过Session查询
            query.query(session);
            //提交事物
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            try{
                //异常回滚
                transaction.rollback();
            }catch (RuntimeException e1){
                e1.printStackTrace();
            }

        }finally {
            session.close();
        }
    }

    /**
     * 带参数查询方法
     * @param query
     * @param <T>
     * @return
     */
    public static <T> T query(Query<T> query){
        //获取Session
        Session session = Hib.sessionFactory().openSession();
        //开启一个事物
        final Transaction transaction = session.beginTransaction();
        T t = null;
        try{
            t = query.query(session);
            transaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            try{
                transaction.rollback();
            }catch (RuntimeException e1){
                e1.printStackTrace();
            }

        }finally {
            session.close();
        }
        return t;
    }
}
