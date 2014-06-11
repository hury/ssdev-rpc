package ctd.net.rpc.transport.sockect.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LengthFieldBasedEncoder extends MessageToByteEncoder<ByteBuf> {
	 private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	 
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int startIdx = out.writerIndex();
        
        out.writeBytes(LENGTH_PLACEHOLDER);
        out.writeBytes(msg);
        int endIdx = out.writerIndex();
        out.setInt(startIdx, endIdx - startIdx - 4);
        
	}

}
