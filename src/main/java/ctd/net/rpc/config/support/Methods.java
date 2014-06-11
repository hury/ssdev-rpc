package ctd.net.rpc.config.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ctd.net.rpc.config.MethodConfig;


public class Methods {
	private  final Map<String,MethodConfig> methodByDescs = new HashMap<String,MethodConfig>();
	private final List<MethodConfig> methodList = new ArrayList<MethodConfig>();
	
	public void addMethod(MethodConfig m){
		
		methodByDescs.put(m.desc(), m);
		methodList.add(m);
		m.setIndex(methodList.size() - 1);
	}
	
	public boolean has(String desc){
		return methodByDescs.containsKey(desc);
	}
	
	public int getCount(){
		return methodList.size();
	}
	
	public void clear(){
		methodByDescs.clear();
		methodList.clear();
	}
	
	public MethodConfig getMethodByDesc(String desc){
		return methodByDescs.get(desc);
	}
	
	public MethodConfig getMethodByName(String name){
		for(MethodConfig m : methodList){
			if(m.getName().equals(name)){
				return m;
			}
		}
		return null;
	}
	
	public MethodConfig getMethodAt(int i){
		if(i >=0 && i < getCount()){
			return methodList.get(i);
		}
		return null;
	}
	
	public List<MethodConfig> getMethods(){
		return methodList;
	}
	
	public MethodConfig getCompatibleMethod(String methodName,Object[] args){
		 for(MethodConfig m : methodList){
				if(m.getName().equals(methodName) && m.isCompatible(args)){
					return m;
				}
		 }
		 return null;
	 }
	
}
