package ctd.net.rpc;

import ctd.net.rpc.Invocation;

public interface Invoker {
	public Object call(Invocation invocation) throws Exception;
}
