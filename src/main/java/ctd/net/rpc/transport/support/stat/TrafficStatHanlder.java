package ctd.net.rpc.transport.support.stat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class TrafficStatHanlder extends ChannelDuplexHandler{
	 
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		 long size = calculateSize(msg);
	     long curtime = System.currentTimeMillis();
	     
	     ctx.fireChannelRead(msg);
	}
	
	 @Override
	 public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
		 long curtime = System.currentTimeMillis();
	     long size = calculateSize(msg);
	     
	     ctx.write(msg, promise);
	 }
	
	
	 private long calculateSize(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).readableBytes();
        }
        if (msg instanceof ByteBufHolder) {
            return ((ByteBufHolder) msg).content().readableBytes();
        }
        return -1;
	 }
}
