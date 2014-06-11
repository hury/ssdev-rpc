package ctd.test.proxy;

import java.lang.reflect.Method;
import java.util.List;

public class TestImpl{
	public String sayHello(){
		return "hello";
	}
	
	public String echo(String msg){
		return msg;
	}
	
	private void innerMethod(){};
	
	protected void subMethod(){};
	
	public static void main(String[] args){
		Class<?> cls = TestImpl.class;
		
		Method[] methods = cls.getDeclaredMethods();
		
		for(Method m : methods){
			System.out.println("modifiers="+m.getModifiers()+",name="+m.getName());
		}
	}

}
