package ctd.net.rpc.transport.sockect;


import ctd.net.rpc.transport.codec.CodecFactory;
import ctd.net.rpc.transport.sockect.codec.LengthFieldBasedDecoder;
import ctd.net.rpc.transport.sockect.codec.LengthFieldBasedEncoder;
import ctd.net.rpc.transport.support.AbstractNettyClientChannelInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;



public class SocketClientChannelInitializer extends AbstractNettyClientChannelInitializer {
	private static final String LENGTH_BASED = "lengthBased";
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pl =  ch.pipeline();
		
		setupEncoders(pl);
		setupDecoders(pl);
		
		if(isEnableLogger()){
			pl.addLast("logger",new LoggingHandler(LogLevel.INFO));
		}
		pl.addLast(getHandler());
	}
	
	private void setupEncoders(ChannelPipeline pl){
		String codec = getCodec();
		if(codec.startsWith(LENGTH_BASED)){
			pl.addLast("objencoder",CodecFactory.getEncoder(codec));
		}
		else{
			pl.addLast("2.lengthdecoder",new LengthFieldBasedEncoder());
			pl.addLast("1.objencoder",CodecFactory.getEncoder(codec));
		}
	}
	
	private void setupDecoders(ChannelPipeline pl){
		String codec = getCodec();
		if(codec.startsWith(LENGTH_BASED)){
			pl.addLast("objdecoder",CodecFactory.getDecoder(codec));
		}
		else{
			pl.addLast("1.lengthdecoder",new LengthFieldBasedDecoder());
			pl.addLast("2.objdecoder",CodecFactory.getDecoder(codec));
		}
	}

	@Override
	public ChannelHandler getHandler() {
		ScoketClientHandler handler = new ScoketClientHandler();
		handler.setCallbackLinstener(callbackListener);
		return handler;
	}
	
}
