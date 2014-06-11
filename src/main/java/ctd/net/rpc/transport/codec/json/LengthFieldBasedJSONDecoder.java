package ctd.net.rpc.transport.codec.json;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class LengthFieldBasedJSONDecoder extends LengthFieldBasedFrameDecoder {

	public LengthFieldBasedJSONDecoder(){
		 super(20971520, 0, 4, 0, 4);
		
	 }

	@Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        
        byte[] bytes = new byte[frame.readableBytes()];
        frame.getBytes(0,bytes);
        return JSON.parse(bytes);
    }
	
	@Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }

}
