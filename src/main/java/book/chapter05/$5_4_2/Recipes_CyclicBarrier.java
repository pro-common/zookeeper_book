package book.chapter05.$5_4_2;
import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*使用CyclicBarrier模拟一个赛跑比赛:
	CyclicBarrier 的字面意思是可循环使用（Cyclic）的屏障（Barrier）。
它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个
线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。CyclicBarrier
默认的构造方法是CyclicBarrier(int parties)，其参数表示屏障拦截的线程
数量，每个线程调用await方法告诉CyclicBarrier我已经到达了屏障，然后当前线程被阻塞。
	在同一个JVM中华，使用CyclicBarrier完全可以解决诸如此类的多线程同步问题。但是，如果是在分布式
环境中有改如何解决呢？Curator中提供的DistributedBarrier就是用来实现分布式Barrier的。
*/
public class Recipes_CyclicBarrier {

	public static CyclicBarrier barrier = new CyclicBarrier( 3 );
	public static void main( String[] args ) throws IOException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool( 3 );
		executor.submit( new Thread( new Runner( "1号选手" ) ) );
		executor.submit( new Thread( new Runner( "2号选手" ) ) );
		executor.submit( new Thread( new Runner( "3号选手" ) ) );
		executor.shutdown();
	}
}
class Runner implements Runnable {
	private String name;
	public Runner( String name ) {
		this.name = name;
	}
	public void run() {
		System.out.println( name + " 准备好了." );
		try {
			Recipes_CyclicBarrier.barrier.await();
		} catch ( Exception e ) {}
		System.out.println( name + " 起跑!" );
	}
}