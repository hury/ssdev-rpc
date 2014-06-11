package ctd.spring;

import java.util.Map;
import java.util.Random;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;




import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.net.rpc.proxy.TestInterface;


public class HelloBean implements TestInterface{
	private AtomicInteger count = new AtomicInteger();
	private Random rd = new Random();
	
	@RpcService
	public String sayHello(){
		throw new IllegalStateException("xxxxx");
	}
	
	@SuppressWarnings("unchecked")
	@RpcService
	public String echo(String s){
//		try {
//			TimeUnit.SECONDS.sleep(1);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Map<String,Object> headers = ContextUtils.get(Context.RPC_INVOKE_HEADERS,Map.class);
		return s  + headers.get("token");
	}
	
	@RpcService
	public void doNothing(String s,int[] i,Long l){
		//Object[] args = new Object[]{1,2,3,(Long)l};
	}
	
	public static void main(String[] args){
		HelloBean h = new HelloBean();
		h.doNothing("", new int[]{1,2}, 0l);
	}

	@Override
	public void doNothing(int i) {
		// TODO Auto-generated method stub
		
	}
	
	@RpcService
	public void onSubscribeMessage(String msg){
		System.out.println("recv subscribe message:" + msg);
	}

	@Override
	public void env(int i, String str, long l) {
		// TODO Auto-generated method stub
		
	}
}
