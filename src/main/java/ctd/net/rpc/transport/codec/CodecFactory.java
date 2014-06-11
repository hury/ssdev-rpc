package ctd.net.rpc.transport.codec;

import io.netty.channel.ChannelHandler;
import ctd.net.rpc.transport.codec.hessian.HessianDecoder;
import ctd.net.rpc.transport.codec.hessian.HessianEncoder;
import ctd.net.rpc.transport.codec.hessian.LengthFieldBasedHessainEncoder;
import ctd.net.rpc.transport.codec.hessian.LengthFieldBasedHessianDecoder;
import ctd.net.rpc.transport.codec.json.JSONDecoder;
import ctd.net.rpc.transport.codec.json.JSONEncoder;
import ctd.net.rpc.transport.codec.json.LengthFieldBasedJSONDecoder;
import ctd.net.rpc.transport.codec.json.LengthFieldBasedJSONEncoder;
import ctd.net.rpc.transport.codec.object.JavaObjectDecoder;
import ctd.net.rpc.transport.codec.object.JavaObjectEncoder;
import ctd.net.rpc.transport.codec.object.LengthFieldBasedJavaObjectDecoder;
import ctd.net.rpc.transport.codec.object.LengthFieldBasedJavaObjectEncoder;

public class CodecFactory {
	public static ChannelHandler getEncoder(String nm){
		switch(nm){
			case Codec.CODEC_OBJECT:
				return new JavaObjectEncoder();
			
			case Codec.CODEC_LENGTH_BASED_OBJECT:
				return new LengthFieldBasedJavaObjectEncoder();
				
			case Codec.CODEC_HESSIAN:
				return new HessianEncoder();
			
			case Codec.CODEC_LENGTH_BASED_HESSIAN:
				return new LengthFieldBasedHessainEncoder();
			
			case Codec.CODEC_JSON:
				return new JSONEncoder();
			
			case Codec.CODEC_LENGTH_BASED_JSON:
				return new LengthFieldBasedJSONEncoder();
				
			default:
				return new HessianEncoder();
				
		}
	}
	
	public static ChannelHandler getDecoder(String nm){
		switch(nm){
			case Codec.CODEC_OBJECT:
				return new JavaObjectDecoder();
			
			case Codec.CODEC_LENGTH_BASED_OBJECT:
				return new LengthFieldBasedJavaObjectDecoder();
				
			case Codec.CODEC_HESSIAN:
				return new HessianDecoder();
			
			case Codec.CODEC_LENGTH_BASED_HESSIAN:
				return new LengthFieldBasedHessianDecoder();
			
			case Codec.CODEC_JSON:
				return new JSONDecoder();
			
			case Codec.CODEC_LENGTH_BASED_JSON:
				return new LengthFieldBasedJSONDecoder();
			
			default:
				return new HessianEncoder();
				
		}
	}
}
