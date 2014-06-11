package ctd.test.proxy;

import ctd.util.annotation.RpcService;

public interface TestInterface<T> {
	public String sayHello();
	public String echo(String msg);
	@RpcService
	public T getObj();
}
