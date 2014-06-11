package ctd.test.hotswap;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.spring.HelloBean;
import ctd.util.ReflectUtil;

public class hotswapTester {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/hotswap/spring-service.xml");
		
		
		while(true){
			DynBeanInterface bean =  (DynBeanInterface)appContext.getBean("hello1");
			System.out.println(bean.sayHello());
			TimeUnit.SECONDS.sleep(15);
			DynFactoryBean factory = (DynFactoryBean) appContext.getBean("&hello1");
			factory.reload();
		}
	}

}
