package ctd.net.rpc.transport;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.transport.exception.TransportException;

public interface Client {
	void setServerUrl(ServerUrl url);
	void connect() throws TransportException;
	Result invoke(Invocation invocation) throws TransportException;
	void disconnect();
}
