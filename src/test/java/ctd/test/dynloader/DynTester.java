package ctd.test.dynloader;

import java.util.concurrent.TimeUnit;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.beans.ServiceBean;

public class DynTester {

	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/dynloader/spring-service.xml");
		ServiceBean<DynBeanInterface>  s  = appContext.getBean("&chis.dynBeans",ServiceBean.class);
		while(true){
			System.out.println(s.getObject().sayHello());
			s.reload();
			TimeUnit.SECONDS.sleep(10);
		}
	}

}
