package book.chapter05.$5_4_2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/*Curator引入了Cache来实现对Zookeeper服务端事件的监听。
Cache是Curator中对事件监听的包装，其对事件的监听其实可以近似看作是一个本地缓存视图和远程Zookeeper视图的对比过程。
同时Curator能够自动为开发人员处理反复注册监听，从而大大简化了原生API开发的繁琐过程。*/

//Cache监听类型 : NodeCache -> 用于监听指定Zookeeper数据节点本身的变化
public class NodeCache_Sample {

    static String path = "/zk-book/nodecache";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("domain1.book.zookeeper:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
	
	public static void main(String[] args) throws Exception {
		//1、启动会话
		client.start();
		//2、创建一个临时节点，并自动递归创建父节点
		client.create()
		      .creatingParentsIfNeeded()
		      .withMode(CreateMode.EPHEMERAL)
		      .forPath(path, "init".getBytes());
		//3、构造一个 NodeCache 实例
	    final NodeCache cache = new NodeCache(client,path,false);
	    //若设置为true，那么在第一次启动的时候会立刻从Zookeeper上读取对应节点的数据内容，并保存在Cache中
		cache.start(true);
		//数据节点本身变更事件监听
		cache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				System.out.println("Node data update, new data: " + 
			    new String(cache.getCurrentData().getData()));
			}
		});
		//4、修改临时节点
		client.setData().forPath( path, "u".getBytes() );
		Thread.sleep( 1000 );
		//5、删除临时节点
		client.delete().deletingChildrenIfNeeded().forPath( path );
		Thread.sleep( Integer.MAX_VALUE );
	}
}