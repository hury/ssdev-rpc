package ctd.net.rpc.beans.loader;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;




public class ServiceBeanLoader<T> {
	
	private T t;
	private String className;
	private long lastModi;
	private String codebase;
	
	public void setClassName(String className){
		this.className = className;
	}
	
	public void setRef(T t){
		if(t == null){
			throw new IllegalArgumentException("null ref object is not allowed.");
		}
		this.t = t;
		className = t.getClass().getName();
		getLastModify();
	}
	
	public long getLastModify(){
		if(lastModi == 0l){
			try {
				Resource r = ClassResourceUtil.getResource(className);
				lastModi = r.lastModified();
			} 
			catch (IOException e) {}
		}
		return lastModi;
	}
	
	public T getRef() {
		if(t == null){
			reload(true);
		}
		return t;
	}

	public void reload(){
		reload(false);
	}
	
	@SuppressWarnings("unchecked")
	public void reload(boolean force){
		try {
			if(StringUtils.isEmpty(className)){
				throw new IllegalStateException("empty className is not allowed.");
			}
			if(codebase == null){
				codebase = ClassResourceUtil.getCodebase(className);
			}
			
			Resource r = ClassResourceUtil.getResource(codebase,className);
			if(!force && t != null && getLastModify() == r.lastModified()){
				return;
			}
			DynamicClassLoader classLoader = new DynamicClassLoader(className);
			classLoader.setCodebase(codebase);
			
			Class<T> clz = (Class<T>)classLoader.loadClass(r.getInputStream(),className);
			T newRef = clz.newInstance();
			if(t != null){
				BeanUtils.copyProperties(t, newRef);
			}
			
			t = newRef;
			lastModi = r.lastModified();

		} 
		catch (Exception e) {
			throw new IllegalStateException("class[" + className + "] not found or init failed",e);
		}
	}

}
