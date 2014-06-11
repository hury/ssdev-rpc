package ctd.test.interfacetest;

import ctd.util.annotation.RpcService;

public interface IInterface2 {
	@RpcService
	public void sayHello(String nm);
}
