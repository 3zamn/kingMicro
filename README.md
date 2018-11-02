![整体架构图](https://github.com/3zamn/ABTestImage/blob/master/20181030104142.jpg) 
# kingMicro是以dubbo为核心的微服务套件。包括服务治理、API网关、降级熔断及服务层流量防护、分布式链路追踪...

项目目录结构：

portal-web：系统请求入口、用springmvc处理resfull请求/权限安全校验等。

portal-html：前端文件、建议单独部署在nginx。

interfere-service：提供分布式系统的服务接口

common：公共类、方法、工具类。

smp:系统管理子系统。

oss:云储存服务。

maven依赖关系：interfere-service —>common。
portal-web —>interfere-service。
smp —>interfere-service。

部署相关：

portal-web：部署在web容器、例如tomcat

portal-html:用nginx代理即可

smp:打包成jar（建议用maven打包）、在java环境运行jar包即可。开发环境用dubbo的main方法启动spring容器、运行app类main方法即可。
开发其他子系统或模块同smp子系统一样单独jar包部署。例子：nohup java -jar smp.jar >smp.log 


架构选型：

权限安全：shiro

分布式调度rpc：dubbo2.6.6+zookeeper

nosql：spring-data-redis,高可用哨兵模式;mongodb存储操作日志、异常信息

数据库：mysql

api文档生成：springfox+swagger2

数据层持久化：mybatis

基础框架：spring、springmvc

数据库连接池：Druid 

定时器：Quartz 2.3

日志管理：SLF4J 1.7、Log4j

分布式文件存储：fastdfs+fastdht

前端框架：vue2.X （双向数据绑定、方便灵巧）、layui、bootstrap

app或第三方应用端：API模块用jwt的token做安全校验

具有如下特点:

1.友好的代码结构及注释，便于阅读及二次开发

2.按业务垂直切分，分布式部署，弹性增加节点、可水平扩展。

3.实现系统的分布式部署、通过dubbo、zookeeper进行rpc调度服务

4.实现前后端彻底分离，通过token进行数据交互，前端再也不用关注后端技术

5.灵活的权限控制，可控制到页面或按钮。实现了功能权限、数据权限，同时支持多级授权，满足绝大部分的权限需求

6.页面交互使用Vue2.x，数据双向绑定、方便实现按钮级别权限控制、极大的提高了开发效率

7.完善的代码生成机制，可生成entity、xml、dao、service、html、js标准的crud代码。同时已封装了高级查询，95%以上的高级查询只需前端按规范传参即可、无需修改java代码。尽可能的实现高灵活、高效率。传参规范说明在swagger接口文档中。

8.引入quartz定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能

9.引入API模板，根据token作为登录令牌，极大的方便了APP接口开发

10.引入Hibernate Validator校验框架，轻松实现后端校验

11.使用自定义注解、aop等实现列表动态列。满足不同用户可自定义列展示列表信息

12.aop切面、根据Transactional事务注解动态实现读写分离

13.引入云存储服务，已支持：七牛云、阿里云、腾讯云及本地分布式文件存储fastdfs、fastdhf等。提供office在线转换高清质量的pdf（使用libreoffice转换）、及可选生成高清图片，同时可灵活设置文字水印、二维码水印。支持在线预览pdf、下载等功能。

14.引入springfox+swagger2支持API接口生成、管理,导出api离线文档（帮助说明模块）

15.封装了大数据Excel导入、导出组件（本地测试200万条数据导出耗时100秒左右、500万条记录导入耗时850秒左右），2007版本及以上的采用分段解析xml文件（07版+Excel底层实现是xml格式,解压后可看到）方式读取Excel文件、导出使用SXSSF方式写入文件，避免读写大数据时内存溢出。使用例子：

    导入：
    LinkedHashMap<Field, Object> map= new LinkedHashMap<>();//可自定义校验
		Method method=SpringContextUtils.getBean(ScheduleJobService.class).getClass().getMethod("saveBatch", List.class);
		ExcelUtil<ScheduleJobLog> upload = new ExcelUtil<>(ScheduleJobLog.class);
		JsonResponse result=upload.importExcel(1, file, map, ScheduleJobService.class, method);	
    导出：
    Query query = new Query(params,ScheduleJobLog.class.getSimpleName());
		ExcelUtil<ScheduleJobLog> export = new ExcelUtil<>(ScheduleJobLog.class);
		Method method=SpringContextUtils.getBean(ScheduleJobService.class).getClass().getMethod("queryScheduleJobLogList", Map.class);
		export.exportExcel("定时任务日志", "定时任务日志", ScheduleJobService.class, method, query,response);
     
提醒：在web-inf/lib下的crypt.jar工具用于加解密配置文件中的密码,例子：
  
    加密：java -cp crypt.ja com.Encrypt 密码 
    解密：java -cp crypt.ja com.Decrypt 密文

 
<a href="http://chenhx.cn:81/#/dashboard/home" target="view_window">服务治理Sentinel平台</a><br>
<a href="http://chenhx.cn:10800/#/monitor/dashboard" target="view_window">链路追踪Skywaking平台</a><br>
<a href="https://github.com/3zamn/kingMicro/blob/master/ABtest.md" target="view_window">性能压测结果</a>

![整体架构图](https://github.com/3zamn/kingMicro/blob/master/20181019154819.png) 
![API gateway](https://github.com/3zamn/ABTestImage/blob/master/API%20Gateway20181102095515.jpg) 


    实现一套完整的以dubbo为基础的微服务套件：
    1. 丰富dubbo服务管理、监控、警告（整合dubbo admin、monitor/扩展添加图形化监控、警告功能等，借鉴dubbokeeper）
    2.使用sentinel 对服务的限流、熔断、降级、服务治理等服务层防护 
    3.使用skywaking 对调用链路追踪、监测、告警等
    4.开发基于openresty的网关、借鉴kong的设计及功能实现，实现应用接入层流量管理、监控、路由、URL重定向、鉴权等系统上流防护
    5.使用disconfig或apollo实现配置中心
   
   
    生命有限！少写重复代码！

QQ交流群：102991119
