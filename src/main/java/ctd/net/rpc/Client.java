package ctd.net.rpc;

import java.util.Map;

import ctd.net.rpc.balance.Balance;
import ctd.net.rpc.balance.BalanceFactory;
import ctd.net.rpc.config.MethodConfig;
import ctd.net.rpc.config.ProviderUrlConfig;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.logger.invoke.InvokeLogBuilder;
import ctd.net.rpc.registry.ServiceRegistry;
import ctd.net.rpc.transport.factory.TransportFactory;
import ctd.spring.AppDomainContext;
import ctd.util.NetUtils;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;

public class Client {
	
	private static final String clientAddress = NetUtils.getLocalHost();
	
	public static Object rpcInvoke(String beanName,String methodName,Object[] parameters,Map<String,Object> headers,Balance balance) throws Exception{
		
		Object v = null; 
		int retryCount = 0;
		ProviderUrlConfig url = null;
		String methodDesc = null;
		InvokeLogBuilder logBuilder = new InvokeLogBuilder();;
		try{
			ServiceRegistry registry = AppDomainContext.getRegistry();
			if(registry == null){
				throw new RpcException(RpcException.REGISTRY_NOT_READY,"registry not ready or disable.");
			}
			ServiceConfig sc = registry.find(beanName);
			MethodConfig mc = sc.getCompatibleMethod(methodName, parameters); 
			if(mc == null){
				throw new RpcException(RpcException.METHOD_NOT_FOUND,"service[" + beanName + "] method[" + methodName + "] paramters is not compatiabled");
			}
			methodDesc = mc.desc();
			
			Invocation invocation = new Invocation();
			invocation.setBeanName(beanName);
			invocation.setMethodDesc(methodDesc);
			invocation.setParameters(parameters);
			
			invocation.setAllHeaders(headers);
			invocation.setHeader(Context.CLIENT_IP_ADDRESS,clientAddress);
			invocation.setHeader(Context.FROM_DOMAIN, AppDomainContext.getName());
		
			logBuilder.setService(sc);
			logBuilder.setMethodDesc(methodDesc);
			logBuilder.setParameters(parameters);
			
			if(balance == null){
				balance = BalanceFactory.getBalance(); 
			}
			int maxRetrys = sc.providerUrlsCount();
			
			while(true){
				url = balance.select(sc.providerUrls());
				if(url == null || maxRetrys == 0){
					throw new RpcException(RpcException.SERVICE_OFFLINE,"service[" + beanName + "] all provider offline.retry again later.");
				}
				logBuilder.setPrivoderUrl(url);
				
				ctd.net.rpc.transport.Client client = TransportFactory.createClient(url.getUrl());
				
				if(client == null){
					throw new RpcException(RpcException.INVAILD_URL,"service[" + beanName + "]@url[" + url.getUrl() + "] is invaild.");
				}
				try{
					Result result = client.invoke(invocation);
					result.throwExpceptionIfHas();
					v = result.getValue();
					logBuilder.success(v);
				}
				catch(Throwable t){
					if(t instanceof RpcException){
						RpcException e = (RpcException)t;
						if(!e.isConnectFailed()){
							throw e;
						}
						retryCount ++;
						if(retryCount <= maxRetrys){
							continue;
						}
					}
					throw (Exception)t;
				}
				return v;
			}
			
		}
		catch(RpcException e){
			logBuilder.exception(e);
			throw e;
		}
		catch(Exception e){
			logBuilder.exception(e);
			throw e;
		}
		finally{
			if(url != null){
				url.increaseInvokeCount(!logBuilder.hasException());
			}
			logBuilder.setRetryCount(retryCount);
			logBuilder.writeLog();
		}
		
	}
	
	public static Object rpcInvoke(String beanName,String methodName,Object[] parameters,Map<String,Object> headers) throws Exception{
		return rpcInvoke(beanName,methodName,parameters,headers,null);
	}
	
	@SuppressWarnings("unchecked")
	public static Object rpcInvoke(String beanName,String methodName,Object ...parameters) throws Exception{
		Map<String,Object> headers = null;
		if(ContextUtils.hasKey(Context.RPC_INVOKE_HEADERS)){
			headers = (Map<String, Object>) ContextUtils.get(Context.RPC_INVOKE_HEADERS);
		}
		return rpcInvoke(beanName,methodName,parameters,headers);
	}
	
	@SuppressWarnings("unchecked")
	public static Object rpcInvoke(String beanName,String methodName,Balance balance,Object ...parameters) throws Exception{
		Map<String,Object> headers = null;
		if(ContextUtils.hasKey(Context.RPC_INVOKE_HEADERS)){
			headers = (Map<String, Object>) ContextUtils.get(Context.RPC_INVOKE_HEADERS);
		}
		return rpcInvoke(beanName,methodName,parameters,headers,balance);
	}
	
	@SuppressWarnings("unchecked")
	public static Object rpcInvoke(String beanName,String methodName) throws Exception{
		Map<String,Object> headers = null;
		if(ContextUtils.hasKey(Context.RPC_INVOKE_HEADERS)){
			headers = ContextUtils.get(Context.RPC_INVOKE_HEADERS,Map.class);
		}
		return rpcInvoke(beanName,methodName,null,headers,null);
	}
}
