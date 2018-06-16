package me.mononing.web.dilechat;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import me.mononing.web.dilechat.provider.AuthRequestFilter;
import me.mononing.web.dilechat.provider.GsonProvider;
import me.mononing.web.dilechat.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

public class Application extends ResourceConfig {
    public Application(){
        // 注册逻辑处理的包名
        //packages("me.mononing.web.dilechat.service.AccountService");
        packages(AccountService.class.getPackage().getName());

        // 注册我们的全局请求拦截器
        register(AuthRequestFilter.class);

        // 注册Json解析器
        //register(JacksonJsonProvider.class);
        //用GsonProvider替换JacksonJsonProvider
        register(GsonProvider.class);
        // 注册日志打印输出
        register(Logger.class);

    }
}
