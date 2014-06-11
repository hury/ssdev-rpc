package ctd.net.rpc.transport.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.net.rpc.Invocation;
import ctd.net.rpc.Result;
import ctd.net.rpc.server.Dispatcher;
import ctd.net.rpc.transport.HeartBeat;
import ctd.net.rpc.transport.sockect.ScoketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

public class AbstractNettyServerHandler extends ChannelInboundHandlerAdapter {

	protected static final Logger logger = LoggerFactory.getLogger(ScoketServerHandler.class);
	protected static final ChannelGroup group =  new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static  ExecutorService exec;
	
	private int threads = Integer.MAX_VALUE;
	private int queues = 0;
	
	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public int getQueues() {
		return queues;
	}

	public void setQueues(int queues) {
		this.queues = queues;
	}

	public AbstractNettyServerHandler(){
		if(exec == null){
			BlockingQueue<Runnable> queue = null;
			if(queues <= 0){
				queue =  new SynchronousQueue<Runnable>();
			}
			else{
				queue = new LinkedBlockingQueue<Runnable>(queues);
			}
			exec = new ThreadPoolExecutor(0, threads,60L, TimeUnit.SECONDS,queue);
		}
	}
	
	@Override  
    public void channelRead(final ChannelHandlerContext ctx,final Object msg) throws Exception {
		if(msg instanceof HeartBeat){
			onClientHeartbeat(ctx);
		}
		else{
			exec.execute(new Runnable() {
				@Override
				public void run() {
					Invocation invocation = (Invocation) msg;
					Result re =  Dispatcher.instance().invoke(invocation);
					ctx.writeAndFlush(re);
				}
			});
		}
	}
	
	
	private void onClientHeartbeat(ChannelHandlerContext ctx) {
		//logger.info("channel[" + ctx.channel().remoteAddress() + "] heartbeat.");
	}


	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
            	logger.info("channel[" + ctx.channel().remoteAddress() + "] readidle and close.");
                ctx.close();
            } 
            else if (e.state() == IdleState.WRITER_IDLE) {
            	//logger.info("channel[" + ctx.channel().remoteAddress() + "] writeidle and send heartbeat.");
                ctx.writeAndFlush(HeartBeat.PING);
            }
        }
    }
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		group.add(ctx.channel());
		logger.info("channel[" + ctx.channel().remoteAddress() + "] active,total=" + group.size());	
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		group.remove(ctx.channel());
		logger.info("channel[" + ctx.channel().remoteAddress() + "] inactive,total=" + group.size());	
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("channel [" + ctx.channel().remoteAddress() + "] closed for exception:" + cause.getMessage());
    }

}
