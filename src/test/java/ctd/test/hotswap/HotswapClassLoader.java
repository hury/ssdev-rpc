package ctd.test.hotswap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class HotswapClassLoader extends URLClassLoader {
	
	public HotswapClassLoader(URL[] urls) {
		super(urls);
	}


	private final ConcurrentHashMap<String, Long> lastModis = new ConcurrentHashMap<String, Long>();
	
	
	public Class<?> loadClass(String basePath,String className) throws IOException{
		String path = basePath + StringUtils.replace(className, ".", "/") + ".class";
		File file = new File(path);
		System.out.println(file.lastModified());
		byte[] data = FileUtils.readFileToByteArray(file);
		return defineClass(className, data, 0, data.length);
	}

}
