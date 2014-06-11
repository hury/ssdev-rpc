package ctd.test.config2;

import com.alibaba.fastjson.JSON;

import ctd.net.rpc.config.MethodConfig;
import ctd.net.rpc.config.ParameterConfig;
import ctd.util.JSONUtils;
import ctd.util.converter.ConversionUtils;

public class MethodConfig2 {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException {
//		MethodConfig m = new MethodConfig();
//		m.setName("hello");
//		
//		ParameterConfig p = new ParameterConfig();
//		p.setId("name");
//		p.setTypeName("java.lang.String");
//		m.addParameter(p);
//		
//		p = new ParameterConfig();
//		p.setId("age");
//		p.setTypeName("int");
//		m.addParameter(p);
//		
//		p = new ParameterConfig();
//		p.setTypeName("void");
//		m.setReturnType(p);
//		
//		String s = JSONUtils.toString(m);
//		System.out.println(s);
//		
//		MethodConfig m2 = JSONUtils.parse(s, MethodConfig.class);
//		System.out.println(JSONUtils.toString(m2));
		
		int i = ConversionUtils.convert("  0",int.class);
		System.out.println(i);
	}

}
