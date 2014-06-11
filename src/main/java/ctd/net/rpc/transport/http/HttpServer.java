package ctd.net.rpc.transport.http;


import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.support.AbstractNettyServerChannelInitializer;
import ctd.net.rpc.transport.support.AbstractNettyServer;


public class HttpServer extends AbstractNettyServer {
	
	public HttpServer(){
		AbstractNettyServerChannelInitializer initializer =  new HttpServerChannelInitializer();
		initializer.setEnableLogger(false);
		setChannelInitializer(initializer);
	}
	
	public HttpServer(ServerUrl serverUrl){
		this();
		setServerUrl(serverUrl);
	}

}
