package ctd.net.rpc.transport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;

public class Invoker {
	private CountDownLatch latch = new CountDownLatch(1);
	private final Invocation invocation;
	private Result result;
	private final long id;
	private long timeout = 20;
	
	public Invoker(long id,Invocation invocation){
		this.id = id;
		invocation.setCorrelationId(id);
		this.invocation = invocation;
	}
	
	public long getId(){
		return id;
	}
	
	public void writeTo(Channel channel) throws Throwable{
		ChannelFuture cf= channel.writeAndFlush(invocation);
		
		if(!latch.await(timeout, TimeUnit.SECONDS)){
			if(cf.isCancelled()){
				throw new InterruptedException("channel write canceled");
			}
			else if(!cf.isSuccess()){
				throw cf.cause();
			}
			throw new TimeoutException("invoke[" + invocation.getCorrelationId() + "] timeout.");
		}
	}

	public Invocation getInvocation() {
		return invocation;
	}
	
	public void setResult(Result result){
		this.result = result;
		latch.countDown();
	}
	
	public Result getResult(){
		return result;
	}

}
