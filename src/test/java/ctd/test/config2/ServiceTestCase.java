package ctd.test.config2;

import ctd.net.rpc.config.ServiceConfig;
import junit.framework.TestCase;

public class ServiceTestCase extends TestCase {
	
	
	public void testServiceConfigInitFromBean(){
		ServiceConfig service = new ServiceConfig();
		service.setAppDomain("chis");
		service.setId("hello");
		service.setProperty("blance", "random");
		assertEquals(service.getId(),"hello");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
