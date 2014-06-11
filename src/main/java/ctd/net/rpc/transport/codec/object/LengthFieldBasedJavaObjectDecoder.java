package ctd.net.rpc.transport.codec.object;

import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

public class LengthFieldBasedJavaObjectDecoder extends ObjectDecoder {

	public LengthFieldBasedJavaObjectDecoder() {
		super(ClassResolvers.weakCachingConcurrentResolver(null));
	}

}
