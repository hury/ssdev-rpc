package ctd.test.netty;

import java.io.IOException;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianFactory;

import ctd.net.rpc.Invocation;

public class HessianTester {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		
		Document doc = DocumentHelper.parseText("<root/>");
				
		
		Invocation invocation = new Invocation();
		invocation.setBeanName("chis.hello");
		invocation.setMethodDesc("void echo(java.lang.String)");
		invocation.setParameters(new Object[]{"sean",new HashMap<String,Object>(),doc});
		
		
		
//		String s = JSON.toJSONString(invocation, SerializerFeature.WriteClassName);
//		System.out.println(s);
//		
//		Invocation  o =JSON.parseObject(s, Invocation.class);
//		System.out.println(o.getBeanName());
	}

}
