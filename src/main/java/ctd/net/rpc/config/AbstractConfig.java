package ctd.net.rpc.config;

import java.util.HashMap;

import ctd.util.converter.ConversionUtils;

public abstract class AbstractConfig implements Config {
	private static final long serialVersionUID = -7889970032838450495L;
	protected HashMap<String,Object> properties;
	protected String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setProperties(HashMap<String,Object> props){
		this.properties = props;
	}
	
	public void setProperty(String nm,Object v){
		if(properties == null){
			properties = new HashMap<String,Object>();
		}
		properties.put(nm, v);
	}
	
	public Object getProperty(String nm){
		if(properties == null){
			return null;
		}
		return properties.get(nm);
	}
	
	public <T> T getProperty(String nm,Class<T> targetType){
		return getProperty(nm,targetType,null);
	}
	
	public <T> T getProperty(String nm,Class<T> targetType,Object defaultValue){
		Object v = null;
		if(properties != null && properties.containsKey(nm)){
			v = getProperty(nm);
		}
		else{
			v = defaultValue;
		}
		return ConversionUtils.convert(v, targetType);
	}
	
	public HashMap<String,Object> getProperties(){
		if(properties == null || properties.size() == 0){
			return null;
		}
		return properties;
	}
	
}
