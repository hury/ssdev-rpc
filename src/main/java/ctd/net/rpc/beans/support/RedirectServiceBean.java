package ctd.net.rpc.beans.support;

import java.util.List;

import ctd.net.rpc.beans.ServiceBean;
import ctd.net.rpc.config.MethodConfig;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.registry.ServiceRegistry;
import ctd.spring.AppDomainContext;

public class RedirectServiceBean<T>  extends ServiceBean<T>{
	private static final long serialVersionUID = 1L;
	
	private String remoteBeanName;
	private String remoteHost;
	

	
	@Override
	public void deploy(){
		try {
			ServiceConfig remoteService = AppDomainContext.getRegistry().find(remoteBeanName);
			List<MethodConfig> remoteMethods =  remoteService.getMethods();
			for(MethodConfig m : remoteMethods){
				this.addMethod(m);
			}
			ServiceRegistry.publish(this);
		} 
		catch (RpcException e) {
			
		}
	}
	
	public String getRemoteBeanName() {
		return remoteBeanName;
	}
	
	public void setRemoteBeanName(String remoteBeanName) {
		this.remoteBeanName = remoteBeanName;
	}
	
	public String getRemoteHost() {
		return remoteHost;
	}
	
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	
}
