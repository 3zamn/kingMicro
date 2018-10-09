  
    
    性能压力测试：
    使用工具：jmeter
    压测系统部署环境配置：centos7、2核CPU Intel(R) Xeon(R) CPU E5-2640 v2 @ 2.00GHz、8G内存
  
    系统部署：smp.jar、oss.jar、portal-web（部署在tomcat）、portal-html（nginx代理）。
             mysql、redis、tomcat、nginx、mongodb、zookeeper,除mysql单独部署在一台机(2核4G内存)，其他都部署上面环境的单节点
    压测接口：/portal-web/sys/user/info
  
    接口业务处理过程：查询当前用户信息。经过shiro权限认证、调用到redis查询(权限、token)、mysql(查询user表、关联查dept表)、mongobd(保存日志)
                      
    压力机：局域网内使用本地电脑
  
    压测数据分组：
    并发50、100、200、300、400、500、600
  
    压测十分钟结果：
      并发：50  ；平均响应时间：75ms  ；tps：665
      并发：100 ；平均响应时间：146ms ；tps：678
      并发：200 ；平均响应时间：287ms ；tps：688
      并发：300 ；平均响应时间：417ms ；tps：701
      并发：400 ；平均响应时间：543ms ；tps：725
      并发：500 ；平均响应时间：693ms ；tps：713
      并发：600 ；平均响应时间：824ms ；tps：718
      
      总体性能表现较佳、tps稳定在700以上！

【峰值QPS和机器计算公式】

	原理：每天80%的访问集中在20%的时间里，这20%时间叫做峰值时间（根据2/8原则）
	公式：( 总PV数 * 80% ) / ( 每天秒数 * 20% ) = 峰值时间每秒请求数(QPS)
	机器：峰值时间每秒QPS / 单台机器的QPS   = 需要的机器（理论值）

	问：假如取tps为560，当前配置的单节点每天可以支撑多少PV？
	答：86400 * 0.2  * 560 (QPS) / 0.8 = 12096000 (PV)
	注意：次结果仅仅是压测一个接口的pv（1.2千万）、正常情况每个pv不止调用一个接口

 可根据业务需要水平扩展。 
---------------------

 
压测结果图：如下
![50并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/50_1.png) 
![50并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/50_2.png) 
![100并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/100_1.png) 
![100并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/100_2.png)
![200并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/200_1.png)
![200并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/200_2.png)
![300并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/300_1.png)
![300并发](https://github.com/3zamn/ABTestImage/blob/master/kingMicro/300_2.png)


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
	
	 线程适当调大，修改server.xml文件
      <Connector port="8088" protocol="HTTP/1.1"
			   maxThreads="2000" minProcessors="10"
			   maxProcessors="500"
               acceptCount="2000"
               connectionTimeout="20000"
               redirectPort="8443" />
	       
	内存适当调大，修改catalina.sh文件cygwin=false darwin=false前面添加
	JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xss256K -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=1024m"
	建议：-Xms和-Xms设置一样大小、避免高并发环境下GC时分配内存出现卡顿等性能不稳问题；
	注意：Metaspace是jdk8以上的参数已代替PermGen 

dubbo简单调优，针对几个参数：

        dubbo.provider.timeout=300000  //超时
        dubbo.threads=2000  //线程设置
        dubbo.payload=209715200  //默认8M。有效载荷即数据包大小、如果传输文件需调大
        更多调优参数请参考dubbo相关文档

    
