# kingMicro
kingMicro 是一套实现分布式、完全前后端分离基础框架。

项目目录结构：

portal-web：系统请求入口、用springmvc处理resfull请求/权限安全校验等。作为消费者、通过rpc调度提供者的服务。

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
开发其他子系统或模块同smp子系统一样单独jar包部署、、、、

架构选型：

权限安全：shiro

分布式调度rpc：dubbo+zookeeper

nosql：spring-data-redis,高可用建议哨兵模式;mongodb存储操作日志、异常信息

数据库：mysql、(已实现动态切面读写分离)

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

2.按业务垂直切分，分布式部署，可水平扩展、弹性增加节点。

3.实现系统的分布式部署、通过dubbo、zookeeper进行rpc调度服务

4.实现前后端彻底分离，通过token进行数据交互，前端再也不用关注后端技术

5.灵活的权限控制，可控制到页面或按钮。实现了功能权限、数据权限，同时支持多级授权，满足绝大部分的权限需求

6.页面交互使用Vue2.x，数据双向绑定、方便实现按钮级别权限控制、极大的提高了开发效率

7.完善的代码生成机制，可生成entity、xml、dao、service、html、js标准的crud代码。同时已封装了高级查询，95%以上的高级查询只需前端按规范传参即可、无需修改java代码。尽可能的实现高灵活、高效率。传参规范说明在swagger接口文档中。

8.引入quartz定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能

9.引入API模板，根据token作为登录令牌，极大的方便了APP接口开发

10.引入Hibernate Validator校验框架，轻松实现后端校验

11.引入云存储服务，已支持：七牛云、阿里云、腾讯云及本地分布式文件存储fastdfs、fastdhf等

12.引入springfox+swagger2支持API接口生成、管理,导出api离线文档（帮助说明模块）

13.封装了大数据Excel导入、导出组件（本地测试200万条数据导出100秒左右、500万条记录导入耗时850秒左右），2007版本及以上的采用分页分段解析xml方式读取Excel文件、导出使用SXSSF方式写入文件，避免读写大数据时内存溢出。使用简单、例子：
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
  
     生命有限！少写重复代码！
     
开发计划：

一：加入两种分布式事务解决方案：1.最大一致性（rocketmq异步事务）、最终一致性（TCC）

二：完善代码生成模块一键生成

三：加入系统字典管理功能、邮件管理模块、个人资料头像上传

四：加入redis缓存，完善统一query封装、查询无需写java代码、仅仅前端添加参数即可

五：加入api网关服务监控模块（kong、openresty或使用orange）

六：集成fastdhf分布式文件管理。

七：优化封装的返回data，完善swagger的生成模拟model参数、响应码。

八：优化token过期、提高用户体验。选用类session会话状态机制（每次请求刷新token过期时间。token放redis缓存中）

九：添加docker支持


