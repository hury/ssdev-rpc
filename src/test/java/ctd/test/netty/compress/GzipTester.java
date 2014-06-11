package ctd.test.netty.compress;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ctd.net.rpc.Invocation;
import ctd.test.netty.TestData;
import ctd.util.xml.XMLHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

public class GzipTester {
	private static String testData;
	private static Invocation invocation;
	
	static{
		try {
			Object doc = XMLHelper.getDocument(GzipTester.class.getResourceAsStream("/ctd/test/netty/compress/example.xml"));
			invocation = new Invocation();
			invocation.setBeanName("chis.hello");
			invocation.setMethodDesc("void echo(java.lang.String)");
			invocation.setParameters(new Object[]{doc});
		} 
		catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void nozip() throws IOException {
		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream outs = new ByteBufOutputStream(buf);
		ObjectOutputStream oouts = new ObjectOutputStream(outs);
		
		try{
			oouts.writeObject(invocation);
			System.out.println("nozip=" + buf.readableBytes());
		}
		finally{
			outs.close();
			buf.release();
		}
	}
	
	public static ByteBuf gzip() throws IOException {
		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream outs = new ByteBufOutputStream(buf);
		GZIPOutputStream gouts = new GZIPOutputStream(outs);
		ObjectOutputStream oouts = new ObjectOutputStream(gouts);
		
		try{
			oouts.writeObject(invocation);
			return buf;
		}
		finally{
			oouts.close();
			System.out.println("gzip=" + buf.readableBytes());
		}
		
	}
	
	public static void ungzip(ByteBuf buf) throws IOException, ClassNotFoundException{
		ByteBufInputStream  ins = new ByteBufInputStream(buf);
		GZIPInputStream gzipins = new GZIPInputStream(ins);
		ObjectInputStream oins = new ObjectInputStream(gzipins);
		
		try{
			Invocation inv  =(Invocation) oins.readObject();
			Document doc = (Document) inv.getParameters()[0];
			
			System.out.println(doc.asXML());
		}
		finally{
			oins.close();
			
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		nozip();
		ungzip(gzip());
		
	}

}
