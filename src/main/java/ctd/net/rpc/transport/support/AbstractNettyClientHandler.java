package ctd.net.rpc.transport.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.net.rpc.Result;
import ctd.net.rpc.transport.CallbackListener;
import ctd.net.rpc.transport.HeartBeat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AbstractNettyClientHandler extends ChannelInboundHandlerAdapter {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractNettyClientHandler.class);
	protected CallbackListener callbackListener;


	public CallbackListener getCallbackLinstener() {
		return callbackListener;
	}

	public void setCallbackLinstener(CallbackListener callbackListener) {
		this.callbackListener = callbackListener;
	}
	

	@Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HeartBeat){
			ctx.writeAndFlush(HeartBeat.PONG);
		}
		else{
			if(callbackListener != null){
				callbackListener.onCallback((Result) msg);
			}
		}
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.error("channel [" + ctx.channel().remoteAddress() + "] closed for exception:" + cause.getMessage());
	}

}
