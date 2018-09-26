  
    
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
      
 可根据业务需要水平扩展。 
 
压测结果图：如下
![50并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/f6e0b8e9bedf0129f55b38593e15958.png) 
![50并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/aa5ade7dd167cc3ac52b8944793205f.png) 
![100并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/ca8dfc0e5a95c77499a373b1f9fa4c5.png) 
![100并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/3700b986cab3e8ba6dc14bae402e714.png)
![200并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/1dede3a3d25d203668913a6bd9a4560.png)
![200并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/f03c78daefce0e35524fffc5a7e95fd.png)
![300并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/3058e33052f016c6cb13d9f161531fb.png) 

下面简单对nginx、tomcat、dubbo rpc性能压测
 nginx 静态页面500并发无业务数据库关联压测结果图：如下
![500并发](https://github.com/3zamn/ABTestImage/blob/master/nginx%E9%9D%99%E6%80%81%E9%A1%B5%E5%8E%8B%E6%B5%8B500%E5%B9%B6%E5%8F%91.jpg) 
![500并发](https://github.com/3zamn/ABTestImage/blob/master/nginx%E9%9D%99%E6%80%81%E9%A1%B5%E5%8E%8B%E6%B5%8B500%E5%B9%B6%E5%8F%911.jpg) 
 tomcat单体应用300并发无业务数据库关联压测结果图：如下
![300并发](https://github.com/3zamn/ABTestImage/blob/master/2%E6%A0%B8CPU_%E5%8D%95%E4%BD%93%E5%BA%94%E7%94%A8%E6%97%A0%E5%85%B3%E8%81%94%E6%95%B0%E6%8D%AE%E5%BA%93300%E5%B9%B6%E5%8F%91.png) 
![300并发](https://github.com/3zamn/ABTestImage/blob/master/2%E6%A0%B8CPU_%E5%8D%95%E4%BD%93%E5%BA%94%E7%94%A8%E6%97%A0%E5%85%B3%E8%81%94%E6%95%B0%E6%8D%AE%E5%BA%93300%E5%B9%B6%E5%8F%911.png)  
 dubbo rpc 300并发无业务数据库关联压测结果图：如下
![300并发](https://github.com/3zamn/ABTestImage/blob/master/2%E6%A0%B8CPU_dubbo%E6%97%A0%E5%85%B3%E8%81%94%E6%95%B0%E6%8D%AE%E5%BA%93300%E5%B9%B6%E5%8F%91.png) 
![300并发](https://github.com/3zamn/ABTestImage/blob/master/2%E6%A0%B8CPU_dubbo%E6%97%A0%E5%85%B3%E8%81%94%E6%95%B0%E6%8D%AE%E5%BA%93300%E5%B9%B6%E5%8F%911.png)  
 
 结果总结：在分别对nginx、tomcat、dubbo已经调优后得出上面结果。常见的调优、对nginx.config修改worker_rlimit_nofile、worker_connections值及添加fastcgi缓存等。
        
      修改系统的打开文件数（tcp链接会打开文件句柄） vim /etc/security/limits.conf 最后添加 
      * soft nofile 655350
      * hard nofile 655350 
      
      马上生效命令： ulimit -n 65535
      查看：ulimit -a  
      
      优化tcp链接的回收时间、TCP TIME_WAIT。修改/etc/sysctl.conf文件，加入以下内容：
      net.ipv4.tcp_syncookies = 1
      net.ipv4.tcp_tw_reuse = 1
      net.ipv4.tcp_tw_recycle = 1
      net.ipv4.tcp_fin_timeout = 30
      
让参数生效： /sbin/sysctl -p
参数的含义：
net.ipv4.tcp_syncookies = 1 表示开启SYN Cookies。当出现SYN等待队列溢出时，启用cookies来处理，可防范少量SYN攻击，默认为0，表示关闭；
net.ipv4.tcp_tw_reuse = 1 表示开启重用。允许将TIME-WAIT sockets重新用于新的TCP连接，默认为0，表示关闭；
net.ipv4.tcp_tw_recycle = 1 表示开启TCP连接中TIME-WAIT sockets的快速回收，默认为0，表示关闭。
net.ipv4.tcp_fin_timeout 修改系統默认的 TIMEOUT 时间

---------------------

	
tomcat简单调优：

      <Connector port="8088" protocol="HTTP/1.1"
			   maxThreads="2000" minProcessors="10"
			   maxProcessors="500"
               acceptCount="2000"
               connectionTimeout="20000"
               redirectPort="8443" />
dubbo简单调优只要针对几个参数：

        dubbo.provider.timeout=300000  //超时
        dubbo.threads=2000  //线程设置
        dubbo.payload=209715200  //默认8M。有效载荷即数据包大小、如果传输文件需调大
        更多调优参数请参考dubbo相关文档

    
