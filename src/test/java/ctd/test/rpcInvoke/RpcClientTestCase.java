package ctd.test.rpcInvoke;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.Client;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RpcClientTestCase extends TestCase {
	private ApplicationContext appContext;

	@Override
	public void setUp(){
		appContext = new ClassPathXmlApplicationContext("ctd/spring/spring-demo.xml");
	}
	
	public void testEhco() throws Exception{
		long start = System.nanoTime();
		for(int i = 0;i < 10000; i ++){
			Object o =  Client.rpcInvoke("chis.hello","echo","hello world");
			if(i % 100 ==0){
				System.out.println(o);
			}
		}
		long end = System.nanoTime();
		System.out.println("timecost=" + (end - start));
		//assertEquals(o, "hello world");
	}
}
