package ctd.net.rpc.transport.codec.json;

import java.io.OutputStream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JSONEncoder extends MessageToByteEncoder<Payload> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Payload msg,ByteBuf out) throws Exception {
		byte compression = msg.getCompression();
		out.writeByte(compression);
		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		
		OutputStream outs = CompressionUtils.buildOutputStream(bout, compression);
		try{
			byte[] bytes = JSON.toJSONBytes(msg, SerializerFeature.WriteClassName);
			msg.setContentLength(bytes.length);
			outs.write(bytes);
		}
		finally{
			outs.close();
			
		}
	}

}
