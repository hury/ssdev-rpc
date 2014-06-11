package ctd.net.rpc.transport.codec.json;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import ctd.net.rpc.Payload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.MessageToByteEncoder;


public class LengthFieldBasedJSONEncoder extends MessageToByteEncoder<Payload> {
	 private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	 
	 @Override
	 protected void encode(ChannelHandlerContext ctx, Payload msg, ByteBuf out) throws Exception {
	        int startIdx = out.writerIndex();
	        byte compression = msg.getCompression();
	        
	        
	        ByteBufOutputStream bout = new ByteBufOutputStream(out);
	       
	        try{
	        	bout.write(LENGTH_PLACEHOLDER);
	        	bout.write(compression);
	        	bout.write(JSON.toJSONBytes(msg, SerializerFeature.WriteClassName));
	        }
	        finally{
	        	bout.close();
	        }
	        
	        int endIdx = out.writerIndex();
	        out.setInt(startIdx, endIdx - startIdx - 4);
	    }

}
