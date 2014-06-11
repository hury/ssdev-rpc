package ctd.test.perf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.Client;

import junit.framework.TestCase;

public class PerfTester extends TestCase {
	private static ApplicationContext appContext;
	private static ExecutorService exec;
	private static final int threads = 101;
	private static final int tasks = 100000;

	public void setUp() {
		appContext = new ClassPathXmlApplicationContext("ctd/test/perf/spring-demo.xml");
		exec = Executors.newFixedThreadPool(threads);
	}

	public PerfTester(String name) {
		super(name);
	}
	
	public void testPerf1() throws Exception{
		final int sub = tasks/(threads-1);
		final CountDownLatch countDown = new CountDownLatch(tasks);
		
		exec.submit(new Runnable() {
			@Override
			public void run() {
				while(!Thread.interrupted()){
					System.out.println("heartbeat...........");
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				System.out.println("heartbeat down.");
				
			}
		});
		
		System.out.println(sub);
		for(int i = 0 ;i < threads - 1; i ++){
			System.out.println("threads=" + i);
			exec.submit(new Runnable() {
				@Override
				public void run() {
					for(int j = 0;j < sub;j ++){
						Object o = null;
						try {
							o = Client.rpcInvoke("chis.hello","echo","hello world");
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
						long count = countDown.getCount();
						if(j % 1000 == 0){
							System.out.println("count=" + count+ ",result=" + o + ",thread=" + Thread.currentThread().getId() + ",j/sub=" + j + "/" + sub);
						}
						countDown.countDown();
					}
					System.out.println("thread out:" + Thread.currentThread().getId());
				}
			});
		}
		
		
		countDown.await();
		exec.shutdownNow();
	}
	
	public static void main(String[] args) throws Exception{
		PerfTester t =new PerfTester("");
		t.setUp();
		long dt1 = System.currentTimeMillis();
		t.testPerf1();
		long dt2 = System.currentTimeMillis();
		long cost = dt2 - dt1;
		double per = cost / tasks;
		System.out.println("cost="+cost+",per=" + per );
	}
}
