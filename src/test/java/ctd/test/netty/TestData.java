package ctd.test.netty;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TestData {
	public static final String STR512B = makeString(512);
	public static final String STR1K = makeString(1024);
	public static final String STR2K = makeString(2*1024);
	public static final String STR5K = makeString(5*1024);
	
	public static String makeString(int len){
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i < len; i++){
			 sb.append((char) (ThreadLocalRandom.current().nextInt(33, 128))); 
		}
		return sb.toString();
	}
}
