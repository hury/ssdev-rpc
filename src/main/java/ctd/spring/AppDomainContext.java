package ctd.spring;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import ctd.net.rpc.registry.ServiceRegistry;
import ctd.util.AppContextHolder;
import ctd.util.NetUtils;

public class AppDomainContext extends AppContextHolder{
	private static CountDownLatch registerInitCountDownLatch = new CountDownLatch(1);
	private static ServiceRegistry registry;
	private static boolean enableLogger;
	  
	public AppDomainContext(){	
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx){
		super.setApplicationContext(ctx);
	}
	
	public static void setRegistryAddress(String address){
		
		
		if(StringUtils.isEmpty(address)){
			ServiceRegistry.setDisable(true);
			setActiveStoreAddress(null);
		}
		else{
			if(address.indexOf("localhost") > 0){
				address = address.replace("localhost", NetUtils.getLocalHost());
			}
			if(address.indexOf("127.0.0.1") > 0){
				address = address.replace("127.0.0.1", NetUtils.getLocalHost());
			}
			setActiveStoreAddress(address);
			registry = new ServiceRegistry(); // ServiceRegistryFactory.getRegistry(address);
			registry.setStore(store);
			registry.start();
		}
		registerInitCountDownLatch.countDown();
	}
	
	public static ServiceRegistry getRegistry(){
		try {
			registerInitCountDownLatch.await();
		} 
		catch(InterruptedException e) {
			
		}
		return registry;
	}
	
	public static void setEnableLogger(boolean status) {
		enableLogger = status;
	}

}
