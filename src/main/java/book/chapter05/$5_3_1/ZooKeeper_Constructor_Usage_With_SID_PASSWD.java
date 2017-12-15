package book.chapter05.$5_3_1;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

//Chapter: 5.3.1 Java API -> 创建连接 -> 创建一个最基本的ZooKeeper对象实例，复用sessionId和session passwd
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {
	
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
        
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper("domain1.book.zookeeper:2181", 
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
        connectedSemaphore.await();
        long sessionId = zookeeper.getSessionId();
        byte[] passwd  = zookeeper.getSessionPasswd();
        
        //使用错误的 sessionId 和 SessionPasswd 来创建 Zookeeper 客户端的实例
        zookeeper = new ZooKeeper("domain1.book.zookeeper:2181", 
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
				1l,//
				"test".getBytes());
        //使用正确的 sessionId 和 SessionPasswd 来创建 Zookeeper 客户端的实例
        zookeeper = new ZooKeeper("domain1.book.zookeeper:2181", 
				5000, //
				new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
				sessionId,//
				passwd);
        Thread.sleep( Integer.MAX_VALUE );
    }
    
    /**
     * 该类实现 Watcher 接口，重写了 process 方法，该方法负责处理来自 Zookeeper
     * 服务端的 Watcher 通知，在收到服务端发来的 SyncConnected 事件之后，解除
     * 主程序在 CountDownLatch 上的等待阻塞。至此，客户端会话创建为完毕。
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event：" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}