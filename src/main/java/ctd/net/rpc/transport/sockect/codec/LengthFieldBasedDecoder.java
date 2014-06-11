package ctd.net.rpc.transport.sockect.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LengthFieldBasedDecoder extends LengthFieldBasedFrameDecoder {
	
	public LengthFieldBasedDecoder(){
		 super(20971520, 0, 4, 0, 4);
	}
	
	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
       
        if (frame == null) {
            return null;
        }
        frame.retain();
        return frame; 
	}
	
	@Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }
}
