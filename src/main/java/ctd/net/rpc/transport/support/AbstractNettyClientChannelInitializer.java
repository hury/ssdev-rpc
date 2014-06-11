package ctd.net.rpc.transport.support;

import ctd.net.rpc.transport.CallbackListener;
import io.netty.channel.ChannelHandler;

public abstract class AbstractNettyClientChannelInitializer extends AbstractNettyChannelInitializer {
	protected CallbackListener callbackListener;

	public abstract ChannelHandler getHandler();

	public CallbackListener getCallbackLinstener() {
		return callbackListener;
	}

	public void setCallbackLinstener(CallbackListener callbackLinstener) {
		this.callbackListener = callbackLinstener;
	}

}
