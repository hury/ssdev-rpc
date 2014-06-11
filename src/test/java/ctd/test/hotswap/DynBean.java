package ctd.test.hotswap;


import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import ctd.test.hotswap.support.DynBeanSupport;
import ctd.util.ReflectUtil;
import ctd.util.annotation.RpcService;

public class DynBean implements DynBeanInterface{
	private String name = "nobody";
	
	@RpcService
	public String sayHello(){
		DynBeanSupport support = new DynBeanSupport();
		return "hello world16,I'm " + name + ",and my age is:" + support.getAge();
	}
	
	public void setName(String nm){
		this.name = nm;
	}
	
	public String getName(){
		return name;
	}

}
