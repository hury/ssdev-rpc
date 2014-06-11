package ctd.net.rpc.transport.http.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestToByteDecoder extends MessageToMessageDecoder<FullHttpRequest> {

	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg,List<Object> out) throws Exception {
		out.add(msg.content());
		msg.retain();
	}

}
