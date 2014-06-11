package ctd.net.rpc.transport.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import ctd.net.rpc.transport.http.codec.ByteToHttpRequestEncoder;
import ctd.net.rpc.transport.http.codec.HttpResponseToByteDecoder;
import ctd.net.rpc.transport.codec.CodecFactory;
import ctd.net.rpc.transport.support.AbstractNettyClientChannelInitializer;

public class HttpClientChannelInitializer extends AbstractNettyClientChannelInitializer {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		 ChannelPipeline pl =  ch.pipeline();
		 
		  pl.addLast("3.encoder",new HttpRequestEncoder())
		  	.addLast("2.bytetorequest",new ByteToHttpRequestEncoder())
			.addLast("1.objectEncoder",CodecFactory.getEncoder(getCodec()))
			
			.addLast("1.decoder",new HttpResponseDecoder())
			.addLast("2.aggregator", new HttpObjectAggregator(1048576))
			.addLast("3.responsetobyte",new HttpResponseToByteDecoder())
			.addLast("4.objectDecoder",CodecFactory.getDecoder(getCodec()));
		
		if(isEnableLogger()){
			pl.addLast("logger",new LoggingHandler(LogLevel.INFO));
		}
		pl.addLast(getHandler());
	}

	@Override
	public ChannelHandler getHandler() {
		HttpClientHandler handler = new HttpClientHandler();
		handler.setCallbackLinstener(callbackListener);
		return handler;
	}

	

}
