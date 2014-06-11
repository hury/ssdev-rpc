package ctd.net.rpc.transport.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {
	public final static byte NOZIP = 0;
	public final static byte GZIP = 1;

	public static InputStream buildInputStream(InputStream is,byte type) throws IOException{
		switch(type){
			case GZIP:
				return new GZIPInputStream(is);
			default:
				return is;
		}
	}
	
	public static OutputStream buildOutputStream(OutputStream os,byte type) throws IOException{
		switch(type){
			case GZIP:
				return new GZIPOutputStream(os);
			default:
				return os;
		}
	}
}
