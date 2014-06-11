package ctd.net.rpc.beans.loader;



import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ClassResourceUtil {
	private static PathMatchingResourcePatternResolver finder = new PathMatchingResourcePatternResolver();
	
	public static Resource getResource(String className){
		String p = StringUtils.replace(className, ".", "/") + ".class";
		Resource r =  finder.getResource(p);
		return r;
	}
	
	public static Resource getResource(String codebase,String className){
		if(StringUtils.isEmpty(codebase)){
			return getResource(className);
		}
		String path;
		String p = StringUtils.replace(className, ".", "/") + ".class";
		if(StringUtils.endsWithIgnoreCase(codebase, "jar")){
			path = codebase + "!/" + p;
		}
		else{
			path = codebase + p;
		}
		try {
			return new UrlResource(path);
		} 
		catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static String getCodebase(String className){
		Resource r = getResource(className);
		try {
			String url = r.getURL().toString();
			if(StringUtils.startsWithIgnoreCase(url, "jar")){
				return StringUtils.substringBefore(url, "!");
			}
			else{
				String p = StringUtils.replace(className, ".", "/") + ".class";
				return StringUtils.substringBefore(url,p);
			}
		} catch (IOException e) {
			return null;
		}
		
	}
}
