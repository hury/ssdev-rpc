package ctd.test.netty;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.transport.http.HttpServer;

public class HttpServerDemo {

	public static void main(String[] args){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/netty/spring-service-http.xml");
	}

}
