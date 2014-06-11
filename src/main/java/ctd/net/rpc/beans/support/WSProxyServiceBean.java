package ctd.net.rpc.beans.support;

import ctd.net.rpc.beans.ServiceBean;

public class WSProxyServiceBean<T> extends ServiceBean<T> {
	private static final long serialVersionUID = -8401146826611084861L;
	private Class<?> interfaceClass;
	private String wsdl;
	
	public void setInterface(String interfaceClassName) throws ClassNotFoundException{
		interfaceClass = Class.forName(interfaceClassName);
	}
	
	@Override 
	public T getObject(){
		return null;
	}
	
	@Override
	public Class<?> getObjectType() {
		if(interfaceClass == null){
			return Object.class;
		}
		return interfaceClass;
	}

	public String getWsdl() {
		return wsdl;
	}

	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}
}
