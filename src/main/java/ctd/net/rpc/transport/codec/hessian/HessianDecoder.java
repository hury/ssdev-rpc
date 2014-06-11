package ctd.net.rpc.transport.codec.hessian;

import java.io.InputStream;
import java.util.List;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.SerializerFactory;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class HessianDecoder extends MessageToMessageDecoder<ByteBuf> {
	 private static final HessianFactory hf = new HessianFactory();
	 static{
		 hf.setSerializerFactory(new SerializerFactory());
	 }

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,List<Object> out) throws Exception {
		 byte compression = msg.readByte();
		 int n = msg.readableBytes();
		 ByteBufInputStream bin = new ByteBufInputStream(msg);
	     InputStream ins = CompressionUtils.buildInputStream(bin, compression);
		 Hessian2Input h2in = hf.createHessian2Input(ins);
	     try{
	    	Payload payload = (Payload) h2in.readObject();
	    	payload.setContentLength(n);
	    	payload.setCompression(compression);
	      	out.add(payload);
	      }
	      finally{
	      	h2in.close();
	      	ins.close();
	      }
	}
}