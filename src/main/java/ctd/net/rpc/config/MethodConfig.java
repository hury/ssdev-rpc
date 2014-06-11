package ctd.net.rpc.config;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ctd.util.ReflectUtil;

public class MethodConfig extends AbstractConfig {
	private static final long serialVersionUID = 4449667363498057868L;
	
	private int index;
	private String name;
	private ParameterConfig returnType;
	private List<ParameterConfig> parameters = new ArrayList<ParameterConfig>();
	private String desc;
	private Method method;
	
	public MethodConfig(){}
	
	public MethodConfig(Method m){
		name = m.getName();
		method = m;
		method.setAccessible(true);
		
		String[] paramNames = ReflectUtil.getMethodParameterNames(m);

		Class<?>[] types =  m.getParameterTypes();
		
		int i = 0;
		for(Class<?> typeClass : types){
			ParameterConfig p = new ParameterConfig(typeClass);
			if(paramNames != null){
				p.setId(paramNames[i]);
			}
			addParameter(p);
			i ++;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public ParameterConfig getReturnType() {
		if(returnType == null){
			returnType = new ParameterConfig();
			returnType.setTypeName("void");
		}
		return returnType;
	}
	
	public void setReturnType(ParameterConfig returnType) {
		this.returnType = returnType;
	}
	
	public List<ParameterConfig> getParameters(){
		return parameters;
	}
	
	public ParameterConfig getParameterAt(int i){
		if(i >=0 && i < parameters.size()){
			return parameters.get(i);
		}
		else{
			throw new IllegalArgumentException("index[" + i + "] overflow.");
		}
	}
	
	public void setParameters(List<ParameterConfig> parameters){
		this.parameters = parameters;
	}
	
	public void addParameter(ParameterConfig parameter){
		parameters.add(parameter);
	}
	
	public int parameterCount(){
		return parameters.size();
	}
	
	public Class<?>[] parameterTypes(){
		int n = parameters.size();
		
		Class<?>[] types = new Class<?>[n];
		
		int i = 0;
		for(ParameterConfig p : parameters){
			types[i] = p.typeClass();
			i ++;
		}
		return types;
	}
	
	public String[] parameterNames(){
		int n = parameters.size();
		if(n == 0){
			return null;
		}
		String[] typeNames = new String[n];
		
		int i = 0;
		for(ParameterConfig p : parameters){
			typeNames[i] = p.getTypeName();
			i ++;
		}
		return typeNames;
	}
	
	public boolean isCompatible(Object[] args){
		boolean result = true;
		if(args == null && parameters.size() == 0){
			return result;
		}
		int i = 0;
		for(ParameterConfig p : parameters){
			Object o = args[i];
			if(!p.isCompatible(o)){
				result = false;
				break;
			}
			i ++;
		}
		return result;
	}
	
	public Object invoke(Object bean,Object[] parameters) throws Exception{
		return method.invoke(bean, parameters);
	}
	
	public String desc(){
		if(desc != null){
			return desc;
		}
		else{
			StringBuilder sb = new StringBuilder(getReturnType().getTypeName());
			sb.append(" ").append(name).append("(");
			for(int i = 0; i < parameters.size(); i ++){
				if(i > 0)
					sb.append(",");
				sb.append(parameters.get(i).getTypeName());
			}
			desc = sb.append(")").toString();
		}
		return desc;
	}
}
