package ctd.net.rpc.proxy;

import ctd.util.annotation.RpcService;

public interface TestInterface {
	@RpcService
	public String echo(String str);
	
	@RpcService
	public void doNothing(int i);
	
	@RpcService
	public void env(int i,String str,long l);
}
