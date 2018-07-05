package test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * 模拟高并发线程安全问题
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月11日
 */
public class CountExample {
  // 请求总数
  public static int clientTotal = 50000000;
  // 同时并发执行的线程数
  public static int threadTotal = 2000;
  public static AtomicInteger count = new AtomicInteger(0);
 // public static int count = 0;
  public static void main(String[] args) throws Exception {
    ExecutorService executorService = Executors.newCachedThreadPool();
    //信号量，此处用于控制并发的线程数
    final Semaphore semaphore = new Semaphore(threadTotal);
    //闭锁，可实现计数器递减
    final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
    for (int i = 0; i < clientTotal ; i++) {
      executorService.execute(() -> {
        try {
         //执行此方法用于获取执行许可，当总计未释放的许可数不超过2000时，
         //允许通行，否则线程阻塞等待，直到获取到许可。
          semaphore.acquire();
          add();
          //释放许可
          semaphore.release();
        } catch (Exception e) {
          //log.error("exception", e);
          e.printStackTrace();
        }
        //闭锁减一
        countDownLatch.countDown();
      });
    }
    countDownLatch.await();//线程阻塞，直到闭锁值为0时，阻塞才释放，继续往下执行
    executorService.shutdown();
    System.out.println("count:"+count);
  }
/*  private static void add() {
    count++;
  }*/
  private static void add() {
	   count.incrementAndGet();
	}
}