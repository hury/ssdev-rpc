package ctd.test.interfacetest;

import ctd.util.annotation.RpcService;

public class ServiceImpl implements IInterface1,IInterface2 {

	@Override
	@RpcService
	public void sayHello(String nm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RpcService
	public void helloWorld() {
		// TODO Auto-generated method stub
		
	}

}
