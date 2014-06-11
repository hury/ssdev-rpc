package ctd.test.netty;

import ctd.net.rpc.transport.Server;
import ctd.net.rpc.transport.factory.TransportFactory;

public class HttpServerTs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = TransportFactory.createServer("http://localhost:9001?codec=hessian");
		server.start();
	}

}
