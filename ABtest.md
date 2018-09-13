  
    
    性能压力测试：
    使用工具：jmeter
    压测系统部署环境配置：centos7、2核CPU Intel(R) Xeon(R) CPU E5-2640 v2 @ 2.00GHz、8G内存
  
    系统部署：smp.jar、oss.jar、portal-web（部署在tomcat）、portal-html（nginx代理）。
             mysql、redis、tomcat、nginx、mongodb、zookeeper全部部署上面环境的单节点
    压测接口：/portal-web/sys/user/info
  
    接口业务处理过程：查询当前用户信息。经过shiro权限认证、调用到redis（权限、token）、mysql（查询user单表）、mongobd（保存日志）
                      
    网络环境：局域网内使用本地电脑
  
    压测数据分组：
    并发50、100、200、300、400、500、600
  
    压测十分钟结果：
      并发：50  ；平均响应时间：93ms  ；tps：570
      并发：100 ；平均响应时间：180ms ；tps：600
      并发：200 ；平均响应时间：355ms ；tps：620
      并发：300 ；平均响应时间：509ms ；tps：640
      并发：400 ；平均响应时间：677ms ；tps：650
      并发：500 ；平均响应时间：852ms ；tps：660
      并发：600 ；平均响应时间：1021ms；tps：650
  
  

    
上述环境单节点压测结果性能较佳！可根据业务需要水平扩展。


    
