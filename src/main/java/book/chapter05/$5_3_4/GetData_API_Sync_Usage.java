package book.chapter05.$5_3_4;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

// ZooKeeper API 获取节点数据内容，使用同步(sync)接口。
public class GetData_API_Sync_Usage implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
    	//父节点路径
    	String path = "/zk-book";
    	//创建会话
    	zk = new ZooKeeper("domain1.book.zookeeper:2181", 
				5000, //
				new GetData_API_Sync_Usage());
        connectedSemaphore.await();
        //创建父节点
        zk.create( path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
        //查看父节点内容
        System.out.println(new String(zk.getData( path, true, stat )));
        //查看父节点的节点状态信息
        System.out.println(stat.getCzxid()+","+stat.getMzxid()+","+stat.getVersion());
        
        zk.setData( path, "456".getBytes(), -1 );
        
        Thread.sleep( Integer.MAX_VALUE );
    }
    
    @Override
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
  	      if (EventType.None == event.getType() && null == event.getPath()) {
  	          connectedSemaphore.countDown();
  	      } else if (event.getType() == EventType.NodeDataChanged) {
  	          try {
  	              System.out.println(new String(zk.getData( event.getPath(), true, stat )));
  	              System.out.println(stat.getCzxid()+","+
  	                                 stat.getMzxid()+","+
  	            		             stat.getVersion());
  	          } catch (Exception e) {}
  	      }
  	    }
    }
}