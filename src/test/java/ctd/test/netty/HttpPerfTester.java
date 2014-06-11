package ctd.test.netty;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.transport.Client;
import ctd.net.rpc.transport.exception.TransportException;
import ctd.net.rpc.transport.factory.TransportFactory;
import ctd.net.rpc.transport.http.HttpClient;


import junit.framework.TestCase;

public class HttpPerfTester extends TestCase {
	private static ExecutorService exec;
	private static final int threads = 21;
	private static final int tasks = 300000;
	private static Client client;

	public void setUp() throws TransportException {
		exec = Executors.newFixedThreadPool(threads);
		client = TransportFactory.createClient("http://localhost:9001?codec=hessian");
	}

	public HttpPerfTester(String name) {
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
						boolean status = false;
						try {
							Invocation invocation = new Invocation();
							invocation.setBeanName("chis.hello");
							invocation.setMethodDesc("void echo(java.lang.String)");
							invocation.setParameters(new String[]{TestData.STR1K});
							
							Result result = client.invoke(invocation);
							o = result.getValue();
							status =invocation.getCorrelationId() == result.getCorrelationId();
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
						long count = countDown.getCount();
						if(j % 1000 == 0){
							System.out.println("status=" + status+ ",thread=" + Thread.currentThread().getId() + ",j/sub=" + j + "/" + sub);
						}
						countDown.countDown();
					}
					System.out.println("thread out:" + Thread.currentThread().getId());
				}
			});
		}
		
		
		countDown.await();
		client.disconnect();
		exec.shutdownNow();
	}
	
	public static void main(String[] args) throws Exception{
		HttpPerfTester t =new HttpPerfTester("");
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
