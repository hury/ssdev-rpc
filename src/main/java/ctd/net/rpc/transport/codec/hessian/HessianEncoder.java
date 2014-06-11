package ctd.net.rpc.transport.codec.hessian;

import java.io.OutputStream;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.SerializerFactory;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HessianEncoder extends MessageToByteEncoder<Payload> {
	 private static final HessianFactory hf = new HessianFactory();
	 static{
		 hf.setSerializerFactory(new SerializerFactory());
	 }
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Payload msg,ByteBuf out) throws Exception {
		byte compression = msg.getCompression();
		ByteBufOutputStream bout = new ByteBufOutputStream(out);
		bout.writeByte(compression);
		
		OutputStream outs = CompressionUtils.buildOutputStream(bout, compression);
		Hessian2Output h2out = hf.createHessian2Output(outs);
		
		try{
			h2out.writeObject(msg);
		}
		finally{
			h2out.close();
			outs.close();
		}
	}

}
