package book.chapter05.$5_4_2;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

//使用Curator实现分布式锁功能
public class Recipes_Lock {

	static String lock_path = "/curator_recipes_lock_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("domain1.book.zookeeper:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    
	public static void main(String[] args) throws Exception {
		//启动会话
		client.start();
		final InterProcessMutex lock = new InterProcessMutex(client,lock_path);
		final CountDownLatch down = new CountDownLatch(1);
		for(int i = 0; i < 10; i++){
			new Thread(new Runnable() {
				public void run() {
					try {
						down.await();
						//分布式锁的获取
						lock.acquire();
					} catch ( Exception e ) {}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.out.println("生成的订单号是 : "+orderNo);
					try {
						//分布式锁的释放
						lock.release();
					} catch ( Exception e ) {}
				}
			}).start();
		}
		down.countDown();
	}
}