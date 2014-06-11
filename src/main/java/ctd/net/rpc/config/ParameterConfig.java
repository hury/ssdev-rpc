package ctd.net.rpc.config;

import ctd.util.ClassHelper;
import ctd.util.ReflectUtil;

public class ParameterConfig extends AbstractConfig {
	private static final long serialVersionUID = -4560472698176743540L;
	private String typeName;
	private Class<?> typeClass;
	
	public ParameterConfig(){};
	
	public ParameterConfig(Class<?> typeClass){
		this.typeClass = typeClass;
		typeName = ReflectUtil.getName(typeClass);
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName){
		this.typeName = typeName;
		try{
			this.typeClass = ClassHelper.forName(typeName);
		}
		catch(ClassNotFoundException e){
			throw new IllegalStateException("typeClass[" + typeName + "] not found");
		}
	}

	public Class<?> typeClass(){
		return typeClass;
	}
	
	public boolean isCompatible(Object o){
		return ReflectUtil.isCompatible(typeClass,o);
	}

}
