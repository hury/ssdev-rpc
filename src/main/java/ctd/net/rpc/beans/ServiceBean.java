package ctd.net.rpc.beans;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import ctd.util.annotation.RpcService;
import ctd.net.rpc.beans.loader.ServiceBeanLoader;
import ctd.net.rpc.config.MethodConfig;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.registry.ServiceRegistry;

public class ServiceBean<T> extends ServiceConfig implements FactoryBean<T> {
	private static final long serialVersionUID = -496458391892972962L;
	protected ServiceBeanLoader<T> beanLoader = new ServiceBeanLoader<T>();

	@Override
	public T getObject() throws Exception {
		return beanLoader.getRef();
	}
	
	public void setRef(T ref){
		beanLoader.setRef(ref);
		setupMethods(ref);
	}
	
	private void setupMethods(T ref){
		Class<?> c = ref.getClass();
		
		Class<?>[] interfaces = c.getInterfaces();
		for(Class<?> f : interfaces){
			Method[] fms = f.getMethods();
			for(Method m : fms){
				if(m.isAnnotationPresent(RpcService.class)){
					 MethodConfig mc = new MethodConfig(m);
				     RpcService an = m.getAnnotation(RpcService.class);
				     String executor = an.executor();
				     if(!StringUtils.isEmpty(executor)){
				    	 mc.setProperty("executor",executor);
				     }
				     addMethod(mc);
				}
			}
		}
		
		Method[] ms = c.getMethods();
	    for(Method m : ms){
	      if(m.isAnnotationPresent(RpcService.class)){
	    	  MethodConfig mc = new MethodConfig(m);
	    	  RpcService an = m.getAnnotation(RpcService.class);
	    	  String executor = an.executor();
	    	  String desc = mc.desc();
	    	  if(!methods.has(desc)){
	    		  if(!StringUtils.isEmpty(executor)){
				    	mc.setProperty("executor",executor);
				  }
	    		  addMethod(mc);
	    	  }
	    	  else{
	    		  if(!StringUtils.isEmpty(executor)){
	    			  methods.getMethodByDesc(desc).setProperty("executor", executor);
	    		  }
	    	  }
	      }
	    }
	}
	
	public void deploy(){
	    if(methods.getCount() > 0){
	    	ServiceRegistry.publish(this);
	    	String subscribe = this.getProperty("subscribe",String.class);
	    	if(!StringUtils.isEmpty(subscribe)){
				if(!methods.has("void onSubscribeMessage(java.lang.String)")){
					throw new IllegalStateException("service[" + id + "] has @subscribe,but [void onSubscribeMessage(java.lang.String)] method not defined.");
				}
			}
	    }
	    else{
	    	throw new IllegalStateException("service[" + id + "] has no method with annotation\'@RpcService\'.its meanless to defined as service");
	    }
	}
	
	public Object invoke(String methodDesc,Object[] parameters) throws Exception{
		MethodConfig mc = getMethodByDesc(methodDesc);
		if(mc == null){
			throw new IllegalStateException("service[" + id + "],method[" + methodDesc + "] not defined.");
		}
		return mc.invoke(getObject(), parameters);
	}
	
	public void reload(boolean force){
		methods.clear();
		beanLoader.reload(force);
		setupMethods(beanLoader.getRef());
	}
	
	public void reload(){
		methods.clear();
		beanLoader.reload(false);
		setupMethods(beanLoader.getRef());
	}
	
	public void setWeights(String weights){
		setProperty("weights",weights);
	}
	
	public void setSubscribe(String subscribe) {
		setProperty("subscribe",subscribe);
	}
	
	public void setSubscribeWay(String way){
		setProperty("subscribeWay",way);
	}

	@Override
	public Class<?> getObjectType() {
		
		try {
			T ref = beanLoader.getRef();
			if(ref != null){
				return ref.getClass();
			}
		}
		catch (Exception e) {
			
		}
		
		return Object.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
