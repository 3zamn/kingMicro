  
    
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
      并发：50  ；平均响应时间：93ms  ；tps：545
      并发：100 ；平均响应时间：180ms ；tps：560
      并发：200 ；平均响应时间：355ms ；tps：570
      并发：300 ；平均响应时间：509ms ；tps：590
      并发：400 ；平均响应时间：677ms ；tps：600
      并发：500 ；平均响应时间：852ms ；tps：590
      并发：600 ；平均响应时间：1021ms；tps：590
  

![50并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/f6e0b8e9bedf0129f55b38593e15958.png) 
![50并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/aa5ade7dd167cc3ac52b8944793205f.png) 
![100并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/3700b986cab3e8ba6dc14bae402e714.png)
![100并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/ca8dfc0e5a95c77499a373b1f9fa4c5.png) 
![200并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/1dede3a3d25d203668913a6bd9a4560.png)
![200并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/f03c78daefce0e35524fffc5a7e95fd.png)
![300并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/3058e33052f016c6cb13d9f161531fb.png) 
![300并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/33e0336cc386e27ef1ac0998ec426da.png) 
 
 
 
 
可根据业务需要水平扩展。


    
