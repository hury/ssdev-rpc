package ctd.test.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.transport.Client;
import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.exception.TransportException;
import ctd.net.rpc.transport.factory.TransportFactory;
import ctd.net.rpc.transport.http.HttpClient;
import ctd.net.rpc.transport.http.HttpServer;

public class HttpClientTs {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		Client client = TransportFactory.createClient("http://localhost:9001?codec=hessian");

		//client.connect();
		
		//FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.POST,"/");
		
		client.connect();
		Invocation invocation = new Invocation();
		invocation.setBeanName("chis.hello");
		invocation.setMethodDesc("void echo(java.lang.String)");
		invocation.setParameters(new String[]{"seanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseanseansean"});
		
//		request.content().writeBytes(JSON.toJSONBytes(invocation, SerializerFeature.WriteClassName));
//		client.getChannel().writeAndFlush(request);
		try{
			Result r = client.invoke(invocation);
			r.throwExpceptionIfHas();
			System.out.println(r.getValue());
		}
		finally{
			client.disconnect();
		}

	}

}
