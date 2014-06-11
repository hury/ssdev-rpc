package ctd.net.rpc.beans;

import java.lang.reflect.Method;

import ctd.net.rpc.config.MethodConfig;

public class LocalServiceBean<T> extends ServiceBean<T> {
	private static final long serialVersionUID = 2095790028533344292L;
	private static final int PUBLIC = 1;
	
	@Override
	public void setRef(T ref){
		super.setRef(ref);
		Class<?> c = ref.getClass();
		Method[] ls = c.getDeclaredMethods();
		for(Method m : ls){
			if(m.getModifiers() == PUBLIC){
				 MethodConfig mc = new MethodConfig(m);
			     addMethod(mc);
			}
		}
	}
	
	@Override
	public void deploy(){
		throw new IllegalStateException("service[" + id + "] is localservice,deploy action is not allowed");
	}

}
