package ctd.net.rpc.transport.support;

import ctd.net.rpc.transport.ServerUrl;

public class AbstractTransport {
	protected ServerUrl serverUrl;
	
	
	public void setServerUrl(ServerUrl serverUrl){
		this.serverUrl = serverUrl;
	}
	
	public void setServerUrl(String url){
		this.serverUrl = new ServerUrl(url);
	}
}
