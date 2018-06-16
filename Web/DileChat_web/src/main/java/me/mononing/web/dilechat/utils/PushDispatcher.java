package me.mononing.web.dilechat.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import me.mononing.web.dilechat.bean.api.base.PushModel;
import me.mononing.web.dilechat.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息推送分发工具类
 */
public class PushDispatcher {
    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    private static final String appId = "fr81tw5dRZ8e1iWGHny3A3";
    private static final String appKey = "mF8EQKWhAt6vWAWbM1QcC4";
    private static final String masterSecret = "K0SRrmQWuU68lYWtK6kOf1";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
    //用来放置需要发送的消息的集合
    private final List<BatchBean> beans = new ArrayList<>();

    private final IIGtPush pusher;

    public PushDispatcher(){
        //基本初始化
        pusher = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 添加需要发送的消息到消息集合中
     * @param receiver 需要发送的对象
     * @param model 发送消息
     * @return 返回添加结果，成功为True，否则为false
     */
    public boolean add(User receiver, PushModel model){
        if (receiver==null||model==null
                ||Strings.isNullOrEmpty(receiver.getPushId())){
            return false;
        }

        if (Strings.isNullOrEmpty(model.getPushString())){
            return false;
        }

        beans.add(buildMessage(receiver.getPushId(),model.getPushString()));
        return true;
    }

    /**
     * 对要发送的数据进行格式化封装
     *
     * @param clientId 接收者的设备Id
     * @param text     要接收的数据
     * @return BatchBean
     */
    private BatchBean buildMessage(String clientId, String text) {
        // 透传消息，不是通知栏显示，而是在MessageReceiver收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(0); //这个Type为int型，填写1则自动启动app

        SingleMessage message = new SingleMessage();
        message.setData(template); // 把透传消息设置到单消息模版中
        message.setOffline(true); // 是否运行离线发送
        message.setOfflineExpireTime(24 * 3600 * 1000); // 离线消息时常

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);

        // 返回一个封装
        return new BatchBean(message, target);
    }

    /**
     * 推送消息提交封装
     * @return 推送成功返回True，否则False
     */
    public boolean submit(){
        //构建打包工具类
        IBatch batch = pusher.getBatch();
        //工具类中是否包含有需要提交的类
        boolean hasBean = false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message,bean.target);
                hasBean = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!hasBean){
            return false;
        }

        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();

            //提交失败时，进行一次重试
            try {
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (result!=null){
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING,"推送服务器响应异常");

        return false;
    }

    //给每个人发送消息的Bean封装
    private static class BatchBean{
        Target target;
        SingleMessage message;
        BatchBean(SingleMessage message,Target target){
            this.target = target;
            this.message = message;
        }
    }
}
