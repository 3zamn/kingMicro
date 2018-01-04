# kingMicro
kingMicro 是一套实现分布式、完全前后端分离基础框架。

项目目录结构：

portal-web：系统请求入口、用springmvc处理resfull请求。作为消费者、通过rpc调度提供者的服务。
portal-html：前端文件、建议单独部署在nginx。
interfere-service：提供分布式系统的服务
common：公共类、方法、工具类。
smp:系统管理子系统。

maven依赖关系：interfere-service —>common。
portal-web —>interfere-service。
smp —>interfere-service。

部署相关：

portal-web：部署在web容器、例如tomcat
portal-html:用nginx代理即可
smp:打包成jar（建议用fat插件打包）、在java环境运行jar包即可。开发环境用dubbo的main方法启动spring容器、运行app类main方法即可。
开发其他子系统或模块同smp子系统一样单独jar包部署、、、、

架构选型：
权限安全：shiro
session管理：spring-session
分布式调度rpc：dubbo+zookeeper
nosql：spring-data-redis。高可用建议哨兵模式：2个监控+1master+2slave
数据库：mysql、(已实现动态切面读写分离)
api文档生成：springfox+swagger2
数据层持久化：mybatis
基础框架：spring、springmvc
数据库连接池：Druid 
定时器：Quartz 2.3
日志管理：SLF4J 1.7、Log4j
分布式文件存储：fastdfs+fastdht
前端框架：vue2.X （双向数据绑定、方便灵巧）、layui、bootstrap
app端：API模块用swt的token做安全校验

具有如下特点:

1.友好的代码结构及注释，便于阅读及二次开发
2.按业务垂直切分，分布式部署，可水平扩展、弹性增加节点。
3.实现系统的分布式部署、通过dubbo、zookeeper进行rpc调度服务
4.实现前后端彻底分离，通过token进行数据交互，前端再也不用关注后端技术
5.灵活的权限控制，可控制到页面或按钮，满足绝大部分的权限需求
6.页面交互使用Vue2.x，数据双向绑定、方便实现按钮级别权限控制、极大的提高了开   	  发效率、引入路由机制，刷新页面会停留在当前页
7.完善的代码生成机制，可生成entity、xml、dao、service、html、js、sql代码
8.引入quartz定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能
9.引入API模板，根据token作为登录令牌，极大的方便了APP接口开发
10.引入Hibernate Validator校验框架，轻松实现后端校验
11.引入云存储服务，已支持：七牛云、阿里云、腾讯云及本地分布式文件存储fastdhf等
12.引入springfox+swagger2支持API接口生成、管理
