package ctd.net.rpc.transport.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ctd.net.rpc.transport.Client;
import ctd.net.rpc.transport.Server;
import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.exception.TransportException;
import ctd.net.rpc.transport.http.HttpClient;
import ctd.net.rpc.transport.http.HttpServer;
import ctd.net.rpc.transport.sockect.SocketClient;
import ctd.net.rpc.transport.sockect.SocketServer;
import ctd.util.lock.KeyEntityRWLockManager;

public class TransportFactory {
	private static final Map<String,Client> clients = new ConcurrentHashMap<>();
	private static final KeyEntityRWLockManager lockManager = new KeyEntityRWLockManager();
	
	public static Client createClient(String url) throws TransportException{
		
		boolean isLocked = lockManager.tryReadLock(url);
		try{
			if(clients.containsKey(url)){
				return clients.get(url);
			}
		}
		finally{
			if(isLocked){
				lockManager.readUnlock(url);
			}
		}
		
		lockManager.writeLock(url);
		try{
			Client client = null;
			if(clients.containsKey(url)){
				client =  clients.get(url);
			}
			else{
				ServerUrl serverUrl = new ServerUrl(url);
				switch(serverUrl.getProtocol()){
					case Protocols.HTTP:
						client = new HttpClient(serverUrl);
						break;
					
					case Protocols.SOCKET:
						client = new SocketClient(serverUrl);
						break;
					
					default:
						throw new IllegalStateException("unsupport client from url[" + url + "]");
				}
				client.connect();
				clients.put(url, client);
			}
			return client;
		}
		finally{
			lockManager.writeUnlock(url);
		}
		
		
		
	}
	
	public static Server createServer(String url){
		ServerUrl serverUrl = new ServerUrl(url);
		switch(serverUrl.getProtocol()){
			case Protocols.HTTP:
				return new HttpServer(serverUrl);
			
			case Protocols.SOCKET:
				return new SocketServer(serverUrl);
			
			default:
				throw new IllegalStateException("unsupport server from url[" + url + "]");
		}
	}
}
