package ctd.net.rpc.transport.sockect;


import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.support.AbstractNettyServerChannelInitializer;
import ctd.net.rpc.transport.support.AbstractNettyServer;

public class SocketServer extends AbstractNettyServer {
	
	public SocketServer(){
		AbstractNettyServerChannelInitializer initializer =  new SocketServerChannelInitializer();
		setChannelInitializer(initializer);
	}
	
	public SocketServer(ServerUrl serverUrl){
		this();
		setServerUrl(serverUrl);
	}

}