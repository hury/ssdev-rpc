package ctd.net.rpc;

import java.util.HashMap;
import java.util.Map;

public class Invocation extends Payload {
	private static final long serialVersionUID = -7883834289264437066L;
	private String beanName;
	private String methodDesc;
	private Object[] parameters;
	private Map<String,Object> headers;
	
	public String getBeanName(){
		return beanName;
	}
	
	public void setBeanName(String name){
		beanName = name;
	}
	
	public void setMethodDesc(String signature){
		methodDesc = signature;
	}
	
	public String getMethodDesc(){
		return methodDesc;
	}
	
	public Object[] getParameters(){
		return parameters;
	}
	
	public void setParameters(Object[] parameters){
		this.parameters = parameters;
	}
	
	public void setHeader(String name,Object v){
		if(headers == null){
			headers = new HashMap<String,Object>();
		}
		headers.put(name, v);
	}
	
	public Object getHeader(String name){
		if(headers == null){
			return null;
		}
		return headers.get(name);
	}
	
	public Map<String,Object> getAllHeaders(){
		return headers;
	}
	
	public void setAllHeaders(Map<String,Object> headers){
		this.headers = headers;
	}


}
