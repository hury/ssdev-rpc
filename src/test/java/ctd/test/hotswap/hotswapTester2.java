package ctd.test.hotswap;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.beans.ServiceBean;
import ctd.spring.HelloBean;
import ctd.util.ReflectUtil;

public class hotswapTester2 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/hotswap/spring-service.xml");

		
		int count = 0;
		while(true){
			DynBeanInterface bean =  (DynBeanInterface)appContext.getBean("chis.hello2");
			System.out.println(bean.sayHello());
			TimeUnit.SECONDS.sleep(5);
			ServiceBean factory = (ServiceBean) appContext.getBean("&chis.hello2");
			factory.reload(true);
			count ++;
			if(count == 20){
				break;
			}
		}
	}

}
