package ctd.net.rpc.transport.codec.object;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JavaObjectEncoder extends MessageToByteEncoder<Payload> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Payload msg,ByteBuf out) throws Exception {
		 byte compression = msg.getCompression();
		 out.writeByte(compression);
		
		 ByteBufOutputStream bout = new ByteBufOutputStream(out);
		 
		 OutputStream outs = CompressionUtils.buildOutputStream(bout, compression);
		 ObjectOutputStream oout = new ObjectOutputStream(outs);
		 try{
			 oout.writeObject(msg);
		 }
		 finally{
			 oout.close();
			 msg.setContentLength(out.readableBytes() - 1);
		 }
	}

}
