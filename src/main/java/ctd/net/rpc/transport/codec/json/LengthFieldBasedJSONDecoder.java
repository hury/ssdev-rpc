package ctd.net.rpc.transport.codec.json;

import java.io.InputStream;

import com.alibaba.fastjson.JSON;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
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
        byte compression = frame.readByte();
        ByteBufInputStream bin = new ByteBufInputStream(frame);
		InputStream ins = CompressionUtils.buildInputStream(bin, compression);
		try{
			int n = frame.readableBytes();
	    	byte[] bytes = new byte[n];
	    	ins.read(bytes, 0, n);
	    	
	    	Payload payload = (Payload)JSON.parse(bytes);
	    	payload.setCompression(compression);
	    	payload.setContentLength(n);
	    	
	    	return payload;
		}
		finally{
			ins.close();
		}

       
    }
	
	@Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }

}
