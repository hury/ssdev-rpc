package ctd.net.rpc.transport.http;

import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.support.AbstractNettyClient;

public class HttpClient extends AbstractNettyClient {

	public HttpClient(){
		HttpClientChannelInitializer initializer = new HttpClientChannelInitializer();
		initializer.setEnableLogger(false);
		setChannelInitializer(initializer); 
	}
	
	public HttpClient(ServerUrl serverUrl){
		this();
		setServerUrl(serverUrl);
	}
}
