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

public class SocketClientDemo {

	public static void main(String[] args) throws RpcException{
		
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/perf/spring-demo.xml");
		
		ServiceRegistry registry = AppDomainContext.getRegistry();
		ServiceConfig sc = registry.find("chis.hello");
		
		Client client = TransportFactory.createClient("socket://localhost:9001?codec=hessian");
		try{
			
			client.connect();
			
			
			Invocation invocation = new Invocation();
			invocation.setBeanName("chis.hello");
			invocation.setMethodDesc(sc.getMethodByName("echo").desc());
			invocation.setParameters(new String[]{TestData.STR2K});
			invocation.setCompression(CompressionUtils.GZIP);
			
			System.out.println(invocation.getMethodDesc());
			
			//for(int i = 0; i < 2; i ++){
				Result result = client.invoke(invocation);
				System.out.println(result.getValue());
				result.throwExpceptionIfHas();
			//}
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		finally{
			client.disconnect();
		}
	}

}
