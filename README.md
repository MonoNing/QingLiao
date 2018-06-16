## 轻聊

#### 项目介绍
>一个基于个推的IM项目

项目整体以Material&nbsp;Design为主题,主界面用BottomNavigationView与Fragment结合。基于第三方推送平台个推实现
即时通信。整个项目开发采用MVP模型。APP除了主APP包外，还依赖于其他四个Library，分别为表情Library，公共资源Library，
文本资源Library，逻辑处理Library。整个项目已经实现了IM项目的核心功能，包含有登录功能，注册功能，搜索群或者人，单聊
或者群聊，聊天功能包含发送图片，发送语音，发送表情。整体项目包含包含前后台，后台使用Jersey RESTful + Hibernate + 
MySQL + Tomcat结构实现。

>以下展示部分软件功能

#####1:运行时动态权限

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![运行时动态权限.gif](https://idlechat.oss-cn-beijing.aliyuncs.com/gif/%E5%8A%A8%E6%80%81%E6%9D%83%E9%99%90.gif)

#####2:注册登录

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![登录注册.gif](https://idlechat.oss-cn-beijing.aliyuncs.com/gif/%E6%B3%A8%E5%86%8C%E7%99%BB%E5%BD%95.gif)


#####3:主界面和搜索用户添加

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![主界面和搜索用户添加.gif](https://idlechat.oss-cn-beijing.aliyuncs.com/gif/%E4%B8%BB%E7%95%8C%E9%9D%A2%E5%92%8C%E6%90%9C%E7%B4%A2%E7%94%A8%E6%88%B7%E6%B7%BB%E5%8A%A0.gif)


#####4:群组聊天

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![群组聊天.gif](https://idlechat.oss-cn-beijing.aliyuncs.com/gif/%E7%BE%A4%E7%BB%84%E8%81%8A%E5%A4%A9.gif)

#####5:创建群组

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![群组聊天.gif](https://idlechat.oss-cn-beijing.aliyuncs.com/gif/%E7%BE%A4%E7%BB%84%E8%81%8A%E5%A4%A9.gif)

#####6:单人聊天

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![单人聊天.gif](https://idlechat.oss-cn-beijing.aliyuncs.com/gif/%E5%8D%95%E4%BA%BA%E8%81%8A%E5%A4%A9.gif)

>第三方框架选取

推送服务 选择个推作为通讯基础

对象存储 阿里OSS 实现对发送图片、语音、头像等文件的存储。方便以后读取。

```java
ext {//依赖库的版本
    //版本相关
    versionCode = 1
    versionName = '1.0.0'

    //相关依赖库
    supportVersion = '25.3.1'
    butterknifeVersion = '8.8.1'

    //一些MD封装布局与Handle封装工具
    geniusVersion = '2.0.0'
    //Glide图片工具
    glideVersion = '3.7.0'

    //圆形图片
    circleimageviewVersion = '2.1.0'
    //图片剪切
    ucropVersion = '2.2.0-native'

    //动态权限辅助工具
    easyPMVersion = "0.3.0"
    //操作云数据库
    ossVersion = '2.3.0'

    //Gson转换工具
    gsonVersion = '2.8.0'
    //retrofit2
    retrofitVersion = '2.1.0'
    //个推的SDK
    getuiVersion = '2.9.3.0'
    //dbflow数据库辅助工具
    dbflowVersion = "4.0.0-beta7"
    //空气面板(处理布局变换与软键盘的收缩)
    airpanelVersion = "1.1.0"
    //Lame 录音 MP3 转码器
    lameVersion = "1.0.0"
}
```

###### 客户端   
网络框架-Retrofit

注解框架-Butterknife

图片框架-Glide

安卓数据库框架-Dbflow

数据存储平台-OSS

###### 服务器  
Jersey-轻量WebService框架

Hibernate-Java数据库操作框架

MySQL-数据库

Gson-数据解析框架

Tomcat-服务器
