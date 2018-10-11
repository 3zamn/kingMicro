package test.queue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
 

/**
 * 生产者线程
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年7月25日
 */
public class Producer implements Runnable {
    
    private volatile boolean  isRunning = true;//是否在运行标志
    private BlockingQueue<String> queue;//阻塞队列
    private static AtomicInteger count = new AtomicInteger();//自动更新的值
    private static final int DEFAULT_RANGE_FOR_SLEEP = 1000;
 
    //构造函数
    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }
 
    public void run() {
        String data = null;
        Random r = new Random();
 
        System.out.println("启动生产者线程！");
        try {
            while (isRunning) {
                System.out.println("正在生产数据...");
                Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));//取0~DEFAULT_RANGE_FOR_SLEEP值的一个随机数
 
                data = "data:" + count.incrementAndGet();//以原子方式将count当前值加1
                System.out.println("将数据：" + data + "放入队列...");
                if (!queue.offer(data, 2, TimeUnit.SECONDS)) {//设定的等待时间为2s，如果超过2s还没加进去返回true
                    System.out.println("放入数据失败：" + data);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("退出生产者线程！");
        }
    }
 
    public void stop() {
        isRunning = false;
    }
}