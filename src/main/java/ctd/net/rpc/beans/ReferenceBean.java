package ctd.net.rpc.beans;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.FactoryBean;

import ctd.net.rpc.proxy.ProxyGenerator;

@SuppressWarnings("rawtypes")
public class ReferenceBean implements FactoryBean {
	private String id;
	private Object proxy;
	private Class<?> interfaceClass;
	private Lock lock = new ReentrantLock(false);
	
	@Override
	public Object getObject() throws Exception {
	    lock.lock();
		if(proxy == null){
			proxy = ProxyGenerator.createProxyBean(id, interfaceClass);
		}
		lock.unlock();
		return proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return interfaceClass;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setInterface(String interfaceClassName) throws ClassNotFoundException{
		interfaceClass = Class.forName(interfaceClassName);
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
	

}
