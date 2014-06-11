package ctd.net.rpc.util;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import ctd.net.rpc.Client;
import ctd.net.rpc.balance.Balance;
import ctd.net.rpc.balance.FixedHostBalance;
import ctd.net.rpc.beans.ServiceBean;
import ctd.net.rpc.config.MethodConfig;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.registry.ServiceRegistry;
import ctd.net.rpc.server.Dispatcher;
import ctd.spring.AppDomainContext;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.util.converter.ConversionUtils;

public class ServiceAdapter {

	public static Object invokeWithUnconvertedParameters(String beanName,String method,List<Object> parametersList) throws Exception{
		if(StringUtils.isEmpty(beanName) || StringUtils.isEmpty(method)){
			throw new RpcException(RpcException.ILLEGAL_ARGUMENTS,"No [serviceId] or [method] arguements.");
		}
		Object result = null;
		ServiceConfig serviceConfig = findLocalServiceConfig(beanName);
		MethodConfig methodConfig = null;
		boolean remoteInvoke = false;
		
		if(serviceConfig == null){
			remoteInvoke = true;
			ServiceRegistry registry = AppDomainContext.getRegistry();
			if(registry == null){
				throw new RpcException(RpcException.REGISTRY_NOT_READY,"registry not ready or disable.");
			}
			serviceConfig = registry.find(beanName);
		}
		
		methodConfig = serviceConfig.getMethodByName(method);
		if(methodConfig != null){
			Class<?>[] parameterTypes =  methodConfig.parameterTypes();
			int parameterCount = parameterTypes.length;
			if(parametersList.size() != parameterCount){
				throw new RpcException(RpcException.ILLEGAL_ARGUMENTS,"service[" + beanName + "] method[" + method + "] must has [" + parameterCount +"] parameters");
			}
			Object[] parameters = convertToParameters(parameterTypes,parametersList);
			
			if(remoteInvoke){
				result = Client.rpcInvoke(beanName, method, parameters);
			}
			else{
				result = invokeLocalService((ServiceBean<?>)serviceConfig,methodConfig.desc(),parameters);
			}
		}
		else{
			throw new RpcException(RpcException.METHOD_NOT_FOUND,"service[" + beanName + "] method[" + method + "] not found");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Object invokeSpecialHostService(String host,String beanName,String method,Object...parameters) throws Exception{
		if(StringUtils.isEmpty(host)){
			return invoke(beanName,method,parameters);
		}
		if(StringUtils.isEmpty(beanName) || StringUtils.isEmpty(method)){
			throw new RpcException(RpcException.ILLEGAL_ARGUMENTS,"No [serviceId] or [method] arguements.");
		}
		Balance balance = new FixedHostBalance(host);
		Map<String,Object> headers = ContextUtils.get(Context.RPC_INVOKE_HEADERS,Map.class);
		return Client.rpcInvoke(beanName, method, parameters,headers,balance);
	}
	
	public static Object invoke(String beanName,String method,Object...parameters) throws Exception{
		if(StringUtils.isEmpty(beanName) || StringUtils.isEmpty(method)){
			throw new RpcException(RpcException.ILLEGAL_ARGUMENTS,"No [serviceId] or [method] arguements.");
		}
		Object result = null;
		ServiceConfig serviceConfig = findLocalServiceConfig(beanName);
		boolean remoteInvoke = false;
		
		if(serviceConfig == null){
			remoteInvoke = true;
			ServiceRegistry registry = AppDomainContext.getRegistry();
			if(registry == null){
				throw new RpcException(RpcException.REGISTRY_NOT_READY,"registry not ready or disable.");
			}
			serviceConfig = registry.find(beanName);
		}
		
		if(remoteInvoke){
			result = Client.rpcInvoke(beanName, method, parameters);
		}
		else{
			MethodConfig mc = serviceConfig.getCompatibleMethod(method, parameters); 
			result = invokeLocalService((ServiceBean<?>)serviceConfig,mc.desc(),parameters);
		}
		return result;
	}
	
	private static Object invokeLocalService(ServiceBean<?> service,String methodDesc,Object... parameters) throws RpcException{
		return Dispatcher.instance().invoke(service, methodDesc, parameters);
	}
	
	public static ServiceConfig findLocalServiceConfig(String beanName){
		String factoryBeanName = "&" + beanName;
		if(AppDomainContext.containBean(factoryBeanName)){
			Object bean = AppContextHolder.getBean(factoryBeanName);
			if(bean instanceof ServiceConfig){
				return (ServiceConfig)bean;
			}
		}
		return null;
	}
	
	public static Object[] convertToParameters(Class<?>[] parameterTypes,List<Object> ls){
		Object[] parameters = new Object[parameterTypes.length];
		int i = 0;
		for(Class<?> type : parameterTypes){
			parameters[i] = ConversionUtils.convert(ls.get(i), type);
			i ++;
		}
		return parameters;
	}
}
