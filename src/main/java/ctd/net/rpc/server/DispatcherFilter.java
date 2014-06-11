package ctd.net.rpc.server;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;

public interface DispatcherFilter {
	
	void beforeInvoke(Invocation invocation);
	void afterInvoke(Result result);

}
