package ctd.net.rpc.transport.support;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class AbstractNettyChannelInitializer extends ChannelInitializer<SocketChannel> {
	protected static int READ_IDLE_SECONDS  = 70; 
	protected static int WRITE_IDLE_SECONDS = 30; 
	protected static int FULL_IDLE_SECONDS  = 0;
	
	private String codec;
	private boolean enableLogger;
	

	public abstract ChannelHandler getHandler();

	public String getCodec() {
		return codec;
	}

	public void setCodec(String codec) {
		this.codec = codec;
	}

	public boolean isEnableLogger() {
		return enableLogger;
	}

	public void setEnableLogger(boolean enableLogger) {
		this.enableLogger = enableLogger;
	}

}
