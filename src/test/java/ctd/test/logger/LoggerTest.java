package ctd.test.logger;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.Client;

public class LoggerTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/logger/spring.xml");
		
		for(int i = 0;i < 1000; i ++){
			Object o =  Client.rpcInvoke("chis.hello","echo","hello world");
			if(i % 100 == 0){
				System.out.println(o);
			}
		}
		//TimeUnit.SECONDS.sleep(20);
		
	}

}
