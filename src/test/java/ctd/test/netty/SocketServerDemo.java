package ctd.test.netty;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ctd.net.rpc.transport.sockect.SocketServer;

public class SocketServerDemo {

	public static void main(String[] args){
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("ctd/test/netty/spring-service-socket.xml");
	}

}
