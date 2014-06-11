package ctd.net.rpc.transport.sockect;


import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.support.AbstractNettyClient;

public class SocketClient extends AbstractNettyClient {
	
	public SocketClient(){
		SocketClientChannelInitializer initializer = new SocketClientChannelInitializer();
		setChannelInitializer(initializer);
	}
	
	public SocketClient(ServerUrl serverUrl){
		this();
		setServerUrl(serverUrl);
	}

}
