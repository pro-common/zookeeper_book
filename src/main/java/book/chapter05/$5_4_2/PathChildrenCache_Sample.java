package book.chapter05.$5_4_2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/*Cache监听类型 : NodeCache -> 用于监听指定Zookeeper数据节点的子节点的变化情况.
1、只能对一级子节点的变更进行事件监听。
2、节点本身和二级子节点之后的都无法进行事件监听。*/
public class PathChildrenCache_Sample {

    static String path = "/zk-book";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("domain1.book.zookeeper:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .sessionTimeoutMs(5000)
            .build();
	public static void main(String[] args) throws Exception {
		//1、启动会话
		client.start();
		//2、构造一个 NodeCache 实例
		PathChildrenCache cache = new PathChildrenCache(client, path, true);
		cache.start(StartMode.POST_INITIALIZED_EVENT);
		//子节点变更事件监听
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework client, 
					               PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("CHILD_ADDED," + event.getData().getPath());
					break;
				case CHILD_UPDATED:
					System.out.println("CHILD_UPDATED," + event.getData().getPath());
					break;
				case CHILD_REMOVED:
					System.out.println("CHILD_REMOVED," + event.getData().getPath());
					break;
				default:
					break;
				}
			}
		});
		//3、创建一个临时节点，初试内容为空
		client.create().withMode(CreateMode.PERSISTENT).forPath(path);
		Thread.sleep( 1000 );
		client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1");
		Thread.sleep( 1000 );
		client.create().withMode(CreateMode.PERSISTENT).forPath(path+"/c1"+"/d1");
		Thread.sleep( 1000 );
		//3、删除一个节点（只能删除叶子节点）
		client.delete().forPath(path+"/c1"+"/d1");
		Thread.sleep( 1000 );
		client.delete().forPath(path+"/c1");
		Thread.sleep( 1000 );
		client.delete().forPath(path);
		Thread.sleep(Integer.MAX_VALUE);
	}
}