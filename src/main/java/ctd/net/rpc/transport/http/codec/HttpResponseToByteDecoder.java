package ctd.net.rpc.transport.http.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpResponseToByteDecoder extends MessageToMessageDecoder<FullHttpResponse> {

	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpResponse msg,List<Object> out) throws Exception {
		out.add(msg.content());
		msg.retain();
	}

}
