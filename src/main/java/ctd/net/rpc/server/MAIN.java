package ctd.net.rpc.server;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@SuppressWarnings("unused")
public class MAIN {
	private static ApplicationContext appContext;

	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("spring/spring.xml");
		
	}

}
