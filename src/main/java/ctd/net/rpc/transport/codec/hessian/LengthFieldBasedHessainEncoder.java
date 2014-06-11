package ctd.net.rpc.transport.codec.hessian;

import java.io.OutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.SerializerFactory;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

public class LengthFieldBasedHessainEncoder extends MessageToByteEncoder<Payload> {
	 private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	 private static final HessianFactory hf = new HessianFactory();
	 static{
		 hf.setSerializerFactory(new SerializerFactory());
	 }
	 
	 @Override
	 protected void encode(ChannelHandlerContext ctx, Payload msg, ByteBuf out) throws Exception {
		 	int startIdx = out.writerIndex();
	        byte compression = msg.getCompression();
	        
	        ByteBufOutputStream bout = new ByteBufOutputStream(out);
	        bout.write(LENGTH_PLACEHOLDER);
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
	        int endIdx = out.writerIndex();
	        out.setInt(startIdx, endIdx - startIdx - 4);
	    }
}
