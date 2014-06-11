package ctd.net.rpc.server;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.beans.ServiceBean;
import ctd.spring.AppDomainContext;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class Dispatcher {
	
	private static Dispatcher instance;
	private DispatcherFilter filter;
	
	public Dispatcher(){
		instance = this;
	}
	
	public static Dispatcher instance(){
		if(instance == null){
			instance = new Dispatcher();
		}
		return instance;
	}
	
	public Result invoke(Invocation invocation)  {
		String beanName = invocation.getBeanName();
		String methodDesc = invocation.getMethodDesc();
		Object[] parameters = invocation.getParameters();
		
		ContextUtils.put(Context.RPC_INVOKE_HEADERS, invocation.getAllHeaders());
		
		ServiceBean<?> service = (ServiceBean<?>)AppDomainContext.getBean("&" + beanName);
		if(service == null){
			throw new IllegalStateException("service[" + beanName + "] not found.");
		}
		
		Result result = null;
		if(filter == null){
			result = invoke(service,methodDesc,parameters);
		}
		else{
			filter.beforeInvoke(invocation);
			result = invoke(service,methodDesc,parameters);
			filter.afterInvoke(result);
		}
		result.setCorrelationId(invocation.getCorrelationId());
		ContextUtils.clear();
		return result;
		
	}
	
	public Result invoke(ServiceBean<?> service,String methodDesc, Object[] parameters){
		Result result = new Result();
		try{
			result.setValue(service.invoke(methodDesc, parameters));
		}
		catch (Throwable t) {
			Throwable cause = t.getCause();
			if(cause != null){
				result.setException(cause);
			}
			else{
				result.setException(t);
			}
		}
		return result;
	}

	public void setFilter(DispatcherFilter filter) {
		this.filter = filter;
	}
}
