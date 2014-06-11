package ctd.net.rpc.transport.http;

import ctd.net.rpc.transport.codec.CodecFactory;
import ctd.net.rpc.transport.http.codec.ByteToHttpResponseEncoder;
import ctd.net.rpc.transport.http.codec.HttpRequestToByteDecoder;
import ctd.net.rpc.transport.support.AbstractNettyServerChannelInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;


public class HttpServerChannelInitializer extends AbstractNettyServerChannelInitializer {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
	
		ChannelPipeline pl =  ch.pipeline();
		pl.addLast("idleStateHandler", new IdleStateHandler(READ_IDLE_SECONDS, WRITE_IDLE_SECONDS, FULL_IDLE_SECONDS));
		
		pl.addLast("1.decoder",new HttpRequestDecoder())
		  .addLast("2.aggregator", new HttpObjectAggregator(1048576))
		  .addLast("3.requesttobyte",new HttpRequestToByteDecoder())
		  .addLast("4.objectdecoder",CodecFactory.getDecoder(getCodec()));
		
		pl.addLast("3.encoder",new HttpResponseEncoder())
		  .addLast("2.bytetoresponse",new ByteToHttpResponseEncoder())
		  .addLast("1.objectencoder",CodecFactory.getEncoder(getCodec()));
			
		if(isEnableLogger()){
			pl.addLast("logger",new LoggingHandler(LogLevel.INFO));
		}
		
		
		pl.addLast("handler",getHandler());	
	}

	@Override
	public ChannelHandler getHandler() {
		HttpServerHandler handler = new HttpServerHandler();
		handler.setQueues(queues);
		handler.setThreads(threads);
		return handler;
	}

}
