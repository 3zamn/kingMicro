package test.rock;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * zookeeper实现分布式锁
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年7月26日
 */
public class ZLock {

    /** zookeeper地址 */
    static final String CONNECT_ADDR = "localhost:2181";
   // static final String CONNECT_ADDR = "192.168.0.4:2181,192.168.0.9:2181,192.168.0.6:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 5000;
    
    static int count = 10;
    public static void genarNo(){
        try {
            count--;
            System.out.println(count);
        } finally {
        
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        //1 重试策略：初试时间为1s 重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //2 通过工厂创建连接
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                    .connectString(CONNECT_ADDR)
                    .sessionTimeoutMs(SESSION_OUTTIME)
                    .retryPolicy(retryPolicy)
//                    .namespace("super")
                    .build();
        //3 开启连接
        cf.start();
        
        //4 分布式锁
        final InterProcessMutex lock = new InterProcessMutex(cf, "/super");
        final CountDownLatch countdown = new CountDownLatch(1);
        
        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countdown.await();
                        //加锁
                        lock.acquire();
                        //-------------业务处理开始
                        genarNo();
                        //-------------业务处理结束
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            //释放
                            lock.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"t" + i).start();
        }
        Thread.sleep(100);
        countdown.countDown();
    }
}