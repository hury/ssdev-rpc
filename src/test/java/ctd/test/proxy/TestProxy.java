package ctd.test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import ctd.net.rpc.proxy.ProxyGenerator;

public class TestProxy implements InvocationHandler{
	
	public TestProxy(){
		
	}
	
	public static void main(String[] args) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
//		TestProxy proxy = new TestProxy();
//		TestInterface o = (TestInterface)proxy.getProxyInstance();
//		System.out.println(o.sayHello());
		
		//TestInterface<Dictionary> bean = (TestInterface)ProxyGenerator.createProxyBean("testService", TestInterface.class);
		//System.out.println(bean);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
	
	public Object getProxyInstance(){  

        return Proxy.newProxyInstance(this.getClass().getClassLoader(),   
        		new Class<?>[]{TestInterface.class}, this);  
    }  

}
