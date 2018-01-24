package book.chapter05.$5_3_2;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

//ZooKeeper API创建节点，使用同步(sync)接口。
public class ZooKeeper_Create_API_Sync_Usage implements Watcher {

	/*一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
	调用 countDown() 的线程打开入口前，所有调用 await 的线程都一直在入口处等待。*/
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{
    	//创建会话
        ZooKeeper zookeeper = new ZooKeeper("domain1.book.zookeeper:2181", 
				5000, //
				new ZooKeeper_Create_API_Sync_Usage());
        connectedSemaphore.await();
        //创建临时节点-EPHEMERAL
        String path1 = zookeeper.create("/zk-test-ephemeral-", 
        		"".getBytes(), 
        		Ids.OPEN_ACL_UNSAFE, 
        		CreateMode.EPHEMERAL);
        System.out.println("Success create znode: " + path1);
        //临时顺序节点-EPHEMERAL_SEQUENTIAL
        String path2 = zookeeper.create("/zk-test-ephemeral-", 
        		"".getBytes(), 
        		Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Success create znode: " + path2);
    }
    
    /**
     * 该类实现 Watcher 接口，重写了 process 方法，该方法负责处理来自 Zookeeper
     * 服务端的 Watcher 通知，在收到服务端发来的 SyncConnected 事件之后，解除
     * 主程序在 CountDownLatch 上的等待阻塞。至此，客户端会话创建为完毕。
     * 
     * WatchedEvent三个基本属性：通知状态keepState、事件类型eventType、节点路径path
     * 
     * 服务端在生成WatcheredEvent事件之后，会调用getWrapper方法将自己包装成一个可序列化的WatcherEvent事件，
     * 以便通过网络传输到客户端。客户端在接受到服务端的这个事件对象后，首先会将WatcherEvent事件还原成一个WatchedEvent
     * 事件，并传递给process方法处理，回调方法process根据入参就能够解析出完整的服务端事件了。
     */
    @Override
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
