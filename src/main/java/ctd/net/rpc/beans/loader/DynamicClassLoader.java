package ctd.net.rpc.beans.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

@SuppressWarnings("unused")
public class DynamicClassLoader extends ClassLoader  {
	private String rootClassName; 
	private String basePackage;
	private String codebase;
	
	public DynamicClassLoader(String rootClassName){
		super(Thread.currentThread().getContextClassLoader());
		this.rootClassName = rootClassName;
		basePackage = StringUtils.substringBeforeLast(rootClassName, ".");
	}
	
	public void setCodebase(String codebase){
		this.codebase = codebase;
	}
	
	
	public boolean isChanged(long lastModi){
		try {
			Resource r = ClassResourceUtil.getResource(codebase,rootClassName);
			return (r.lastModified() != lastModi);
		} 
		catch (IOException e) {
		}
		return false;
	}
	
	
	public Class<?> reload(){
		return reloadClass(rootClassName);
	}
	
	private Class<?> reloadClass(String className){
		try {
			Resource r = ClassResourceUtil.getResource(className);
			InputStream is = r.getInputStream();
			return loadClass(is,className);
		} 
		catch (FileNotFoundException e) {
			throw new IllegalStateException("class[" + className +"] file not found.");
		}
		catch (IOException e) {
			throw new IllegalStateException("class[" + className +"] IO Exception:",e);
		}
	}
	
	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException{
		if(StringUtils.startsWith(name,basePackage)){
			Class<?> clz = Class.forName(name);
			if(!clz.isInterface()){
				return reloadClass(name);
			}
		}
		return super.loadClass(name, resolve);
	}
	
	
	public Class<?> loadClass(String path,String className) throws IOException{
		File file = new File(path);
		return loadClass(file,className);
	}
	
	public Class<?> loadClass(File file,String className) throws IOException{
		byte[] data = FileUtils.readFileToByteArray(file);
		return loadClass(data,className);
	}
	
	public Class<?> loadClass(InputStream is,String className) throws IOException{
		byte[] data = IOUtils.toByteArray(is);
		return loadClass(data,className);
	}
	
	public Class<?> loadClass(byte[] data,String className) throws IOException{
		return defineClass(className, data, 0, data.length);
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

}
