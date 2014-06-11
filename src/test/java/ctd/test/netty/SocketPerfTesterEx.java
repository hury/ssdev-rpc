package ctd.test.netty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.context.support.ClassPathXmlApplicationContext;



import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.transport.Client;
import ctd.net.rpc.transport.compression.CompressionUtils;
import ctd.net.rpc.transport.exception.TransportException;
import ctd.net.rpc.transport.factory.TransportFactory;
import ctd.net.rpc.transport.sockect.SocketClient;
import ctd.spring.AppDomainContext;

import junit.framework.TestCase;

public class SocketPerfTesterEx extends TestCase {
	private static ExecutorService exec;
	private static final int threads = 51;
	private static final int tasks = 300000;
	private static ClassPathXmlApplicationContext appContext;

	public void setUp() throws RpcException {
		exec = Executors.newFixedThreadPool(threads);
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/perf/spring-demo.xml");
		AppDomainContext.getRegistry().find("chis.hello");
	}

	public SocketPerfTesterEx(String name) {
		super(name);
	}
	
	public void testPerf1() throws Exception{
		final int sub = tasks/(threads-1);
		final CountDownLatch countDown = new CountDownLatch(tasks);
		
		exec.submit(new Runnable() {
			@Override
			public void run() {
				while(!Thread.interrupted()){
					System.out.println("...........");
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
						String result = null;
						int token = ThreadLocalRandom.current().nextInt(0,10000);
						try {
							Map<String,Object> map = new HashMap<>();
							map.put("token", token);
							result =  (String) ctd.net.rpc.Client.rpcInvoke("chis.hello", "echo",new Object[]{TestData.STR512B},map);
							if(j % 1000 == 0){
								System.out.println(token + "," + ",thread=" + Thread.currentThread().getId() + ",j/sub=" + j + "/" + sub);
							}
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
						//long count = countDown.getCount();
						
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
		SocketPerfTesterEx t =new SocketPerfTesterEx("");
		t.setUp();
		long dt1 = System.currentTimeMillis();
		t.testPerf1();
		long dt2 = System.currentTimeMillis();
		double cost = (dt2 - dt1);
		double per = cost / tasks;
		double qos =  tasks / (cost / 1000);
		System.out.println("cost="+cost+",per=" + per + ",qps(tps)=" + qos );
	}
}
