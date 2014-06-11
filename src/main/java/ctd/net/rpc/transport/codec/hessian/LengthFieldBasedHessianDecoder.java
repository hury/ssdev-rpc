package ctd.net.rpc.transport.codec.hessian;

import java.io.InputStream;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.SerializerFactory;

import ctd.net.rpc.Payload;
import ctd.net.rpc.transport.compression.CompressionUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LengthFieldBasedHessianDecoder extends LengthFieldBasedFrameDecoder{
	 private static final HessianFactory hf = new HessianFactory();
	 static{
		 hf.setSerializerFactory(new SerializerFactory());
	 }
	 
	public LengthFieldBasedHessianDecoder(){
		 super(20971520, 0, 4, 0, 4);
	}
	
	@Override
   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        byte compression = frame.readByte();
        int n = frame.readableBytes();
        
        ByteBufInputStream bin = new ByteBufInputStream(frame);
        InputStream ins = CompressionUtils.buildInputStream(bin, compression);
        
        Hessian2Input h2in = hf.createHessian2Input(ins);
        try{
        	Payload payload =  (Payload) h2in.readObject();
        	payload.setContentLength(n);
        	payload.setCompression(compression);
        	
        	return payload;
        }
        finally{
        	h2in.close();
        	ins.close();
        }
	}
	
	@Override
   protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
       return buffer.slice(index, length);
   }
}
