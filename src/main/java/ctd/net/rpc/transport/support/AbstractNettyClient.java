package ctd.net.rpc.transport.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.transport.CallbackListener;
import ctd.net.rpc.transport.Invoker;
import ctd.net.rpc.transport.exception.TransportException;

public class AbstractNettyClient extends AbstractClient implements CallbackListener{
	private final static AtomicLong InvokerId = new AtomicLong();
	private final static ConcurrentHashMap<Long,Invoker> invokers = new ConcurrentHashMap<Long,Invoker>();
	private final static EventLoopGroup group = new NioEventLoopGroup();
	private final Lock connectingLock = new ReentrantLock();
	
	private Bootstrap bootstrap;
	private AbstractNettyClientChannelInitializer channelInitializer;
	protected Channel channel;
	private int connectTimeout = 10000;
	
	@Override
	public void connect()  throws TransportException {	
		connectingLock.lock();
		try{
			if(bootstrap == null){
				channelInitializer.setCodec(serverUrl.getCodec());
				
				bootstrap = new Bootstrap();
				bootstrap.group(group)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE,true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
					.handler(channelInitializer);
			}
			if(channel != null && channel.isActive()){
				return;
			}
		
			String host = serverUrl.getHost();
			int port = serverUrl.getPort();
			ChannelFuture f= bootstrap.connect(host, port);
			f.awaitUninterruptibly();
			if(f.isCancelled()){
				throw new TransportException(TransportException.CONNECT_FALIED);
			}
			else if(!f.isSuccess()){
				throw new TransportException(TransportException.CONNECT_FALIED,f.cause());
			}
			channel = f.channel();
		}
		finally{
			connectingLock.unlock();
		}
	}
	
	public void checkAndConnect() throws TransportException{
		if(channel == null || !channel.isActive()){
			connect();
		}
	}
	
	@Override
	public Result invoke(Invocation invocation) throws TransportException{
		checkAndConnect();
		
		Invoker invoker = new Invoker(InvokerId.getAndIncrement(),invocation);
		invokers.put(invoker.getId(), invoker);
		
		try {
			invoker.writeTo(channel);
			return invoker.getResult();
		} 
		catch (InterruptedException e) {
			throw new TransportException(TransportException.INTERRUPTED,e);
		}
		catch (TimeoutException e) {
			throw new TransportException(TransportException.TASK_TIMEOUT,e);
		}
		catch(java.net.ConnectException e){
			throw new TransportException(TransportException.CONNECT_FALIED,e);
		}
		catch (Throwable e) {
			throw new TransportException(TransportException.UNKNOWN,e);
		}
		finally{
			invokers.remove(invoker.getId());
		}
		
	}
	
	@Override
	public void onCallback(Result result){
		long id = result.getCorrelationId();
		if(invokers.containsKey(id)){
			invokers.get(id).setResult(result);
		}
	}

	@Override
	public void disconnect() {
		if(channel != null && channel.isActive()){
			try {
				channel.close().sync();
			} 
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public void setChannelInitializer(AbstractNettyClientChannelInitializer channelInitializer) {
		channelInitializer.setCallbackLinstener(this);
		this.channelInitializer = channelInitializer;
	}
	
	public Channel getChannel(){
		return channel;
	}

}
