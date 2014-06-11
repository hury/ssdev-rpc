package ctd.net.rpc.transport.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class AbstractNettyServer extends AbstractServer {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractNettyServer.class);
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel serverChannel;
	private AbstractNettyServerChannelInitializer channelInitializer;
	
	@Override
	public void start() {
		bossGroup = new NioEventLoopGroup();
	    workerGroup = new NioEventLoopGroup(10);
	    
	    String host = serverUrl.getHost();
		int port = serverUrl.getPort(); 
		
		 try{	
			channelInitializer.setCodec(serverUrl.getCodec());
			channelInitializer.setThreads(threads);
			channelInitializer.setQueues(queues);
			
			bootstrap = new ServerBootstrap();
		    bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(channelInitializer)
		    .option(ChannelOption.SO_BACKLOG, 512)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
		    beforeStart();
		    
		    ChannelFuture f = bootstrap.bind(host,port).sync();
		    serverChannel = f.channel();
		    afterStart();
	    } 
		catch (InterruptedException e) {
			throw new IllegalStateException("RpcServer[" + serverUrl + "] start failed.",e);
		}
   
	}

	private void beforeStart() {
		
	}

	private void afterStart() {
		logger.info("RpcServer[" + serverUrl + "] [threads=" + threads + "] [queues=" + queues + "] started.");
	}

	@Override
	public void shutdown() {
		try {
			if(serverChannel != null && serverChannel.isActive()){
				serverChannel.close().sync();
			}
			if(bossGroup != null){
				bossGroup.shutdownGracefully();
			}
			if(workerGroup != null){
				workerGroup.shutdownGracefully();
			}
		} 
		catch (InterruptedException e) {
			
		}
	}
	
	public void setChannelInitializer(AbstractNettyServerChannelInitializer channelInitializer) {
		this.channelInitializer = channelInitializer;
	}
}
