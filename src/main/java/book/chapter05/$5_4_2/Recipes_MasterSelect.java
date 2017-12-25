package book.chapter05.$5_4_2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/*Master选举*/
public class Recipes_MasterSelect {

	static String master_path = "/curator_recipes_master_path";
	
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("domain1.book.zookeeper:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    
    public static void main( String[] args ) throws Exception {
    	//1、启动会话
    	client.start();
    	//2、创建LeaderSelector实例，负责封装所有和Master选举相关的逻辑，包括所有和Zookeeper服务器的交互过程。
        LeaderSelector selector = new LeaderSelector(client, 
        		master_path, 
        		new LeaderSelectorListenerAdapter() {
        		    //在竞争到Master后自动调用该方法，一旦执行完takeLeadership方法，就会立即释放Master权利，然后重新开始新一轮的Master选举。
		            public void takeLeadership(CuratorFramework client) throws Exception {
		                System.out.println("成为Master角色");
		                Thread.sleep( 3000 );
		                System.out.println( "完成Master操作，释放Master权利" );
		            }
		        });
        selector.autoRequeue();
        selector.start();
        Thread.sleep( Integer.MAX_VALUE );
	}
}