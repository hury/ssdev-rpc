package ctd.test.interfacetest;

import java.lang.reflect.Method;

import ctd.net.rpc.beans.ServiceBean;

public class App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServiceImpl ref = new ServiceImpl();
		
		ServiceBean<ServiceImpl> sb = new ServiceBean<ServiceImpl>();
		sb.setRef(ref);
		
		System.out.println(sb.desc());
		
		/*
		
		Class<?> c = ref.getClass();
		Method[] mes = c.getMethods();
		for(Method m : mes){
			System.out.println(m.toString());
		}
		
		Class<?>[] interfaces = c.getInterfaces();
		
//		for(Class<?> inter : interfaces){
//			Method[] mes = inter.getMethods();
//			for(Method m : mes){
//				m.get
//				System.out.println(m.getName());
//			}
//		}
 * 
 * 
 */
	}

}
