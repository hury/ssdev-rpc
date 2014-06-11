package ctd.net.rpc.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ctd.net.rpc.acl.ServiceACL;
import ctd.net.rpc.acl.ServiceACLItem;
import ctd.net.rpc.config.support.Methods;
import ctd.util.JSONUtils;
import ctd.util.acl.ACListType;


public class ServiceConfig extends AbstractConfig{
	private static final long serialVersionUID = 335327090899721253L;
	private static final Log logger = LogFactory.getLog(ServiceConfig.class);
	private String domain;
	protected Methods methods = new Methods();
	protected transient CopyOnWriteArraySet<ProviderUrlConfig> providerUrls = new CopyOnWriteArraySet<ProviderUrlConfig>();
	protected transient ServiceACL ACL = new ServiceACL();
	
	public void setAppDomain(String domain){
		this.domain = domain;
	}
	
	public String getAppDomain(){
		return domain;
	}
	
	public int providerUrlsCount(){
		return providerUrls.size();
	}
	
	 public void addProviderUrl(String url){
    	providerUrls.add(new ProviderUrlConfig(url));
    }
	 
	 public void updateProviderUrls(List<ProviderUrlConfig> urls){
    	if(urls.size() == 0){
    		providerUrls.clear();
    		logger.warn("service[" + id + "] all providers offline.");
    	}
    		
		for(ProviderUrlConfig url : urls){
			if(providerUrls.add(url)){
				logger.info("service[" + id + "@" + url.getHost() + "] online.");
			}
		}
		
		for(ProviderUrlConfig url : providerUrls){
			if(!urls.contains(url)){
				providerUrls.remove(url);
				logger.info("service[" + id + "@" + url.getHost() + "] offline");
			}
		}
    }
    
    public void removeProviderUrl(String urlStr){
    	providerUrls.remove(new ProviderUrlConfig(urlStr));
    }
    
    public void removeProviderUrl(ProviderUrlConfig url){
    	providerUrls.remove(url);
    }
    
    public List<ProviderUrlConfig> providerUrls(){
    	List<ProviderUrlConfig> ls = new ArrayList<ProviderUrlConfig>();
    	for(ProviderUrlConfig urlc : providerUrls){
    		ls.add(urlc);
    	}
    	return ls;
    }
    
    public void updateACL(ACListType type,List<String> ls){
    	ACL.setType(type);
    	List<ServiceACLItem> items = new ArrayList<ServiceACLItem>();
    	if(ls != null){
	    	for(String s : ls){
	    		items.add(new ServiceACLItem(s));
	    	}
    	}
    	ACL.updateAll(items);
    }
    
    public ServiceACL lookupACL(){
    	return ACL;
    }
	
    public List<MethodConfig> getMethods(){
    	return methods.getMethods();
    }
    
    public void setMethods(List<MethodConfig> methodList){
    	if(methods.getCount() > 0){
    		methods.clear();
    	}
    	for(MethodConfig m : methodList){
    		methods.addMethod(m);
    	}
    }
    
    public void addMethod(MethodConfig mc){
    	methods.addMethod(mc);
    }
    
    public MethodConfig getMethodByDesc(String desc){
    	return methods.getMethodByDesc(desc);
    }
    
    public MethodConfig getMethodByName(String name){
    	return methods.getMethodByName(name);
    }
    
    public MethodConfig getCompatibleMethod(String methodName,Object[] args){
    	return methods.getCompatibleMethod(methodName, args);
    }
    
    @Override
    public boolean equals(Object v){
		if(v == null || !ServiceConfig.class.isInstance(v)){
			return false;
		}
		ServiceConfig o = (ServiceConfig)v;
		String sid = o.getId();
    	if(sid != null && id != null &&  !sid.equals(id)){
    		return false;
    	}
    	List<MethodConfig> ls = o.getMethods();
    	if(ls.size() != methods.getCount()){
    		return false;
    	}
    	for(MethodConfig m : ls){
    		if(!methods.has(m.desc())){
    			return false;
    		}
    	}
    	return true;
    }
    
    public String desc(){
    	return JSONUtils.toString(this,ServiceConfig.class);
    }
    
    public static ServiceConfig parse(String desc){
    	return JSONUtils.parse(desc, ServiceConfig.class);
    }

}
