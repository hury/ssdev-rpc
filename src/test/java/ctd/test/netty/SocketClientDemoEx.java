package ctd.test.netty;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.registry.ServiceRegistry;
import ctd.net.rpc.transport.Client;
import ctd.net.rpc.transport.compression.CompressionUtils;
import ctd.net.rpc.transport.factory.TransportFactory;
import ctd.spring.AppDomainContext;

public class SocketClientDemoEx {

	public static void main(String[] args) throws Exception{
		
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/perf/spring-demo.xml");
		
		for(int i = 0; i < 100; i ++){
			String result =  (String) ctd.net.rpc.Client.rpcInvoke("chis.hello", "echo",new Object[]{TestData.STR512B});
			System.out.println(result);
		}
	}

}
