package ctd.net.rpc.transport.codec.json;


import java.io.InputStream;
import java.util.List;

import com.alibaba.fastjson.JSON;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class JSONDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,List<Object> out) throws Exception {
		byte compression = msg.readByte();
		
		ByteBufInputStream bin = new ByteBufInputStream(msg);
		InputStream ins = CompressionUtils.buildInputStream(bin, compression);
	    try{
	    	int n = msg.readableBytes();
	    	byte[] bytes = new byte[n];
	    	ins.read(bytes, 0, n);
	    	
	    	Payload payload = (Payload)JSON.parse(bytes);
	    	payload.setCompression(compression);
	    	payload.setContentLength(n);
	    	
	    	out.add(payload);
	    }
	    finally{
	    	ins.close();
	    }
	}

}
