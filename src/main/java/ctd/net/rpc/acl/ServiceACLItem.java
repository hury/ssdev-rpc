package ctd.net.rpc.acl;

import org.apache.commons.lang3.StringUtils;

public class ServiceACLItem {
	private String token;
	
	public ServiceACLItem(String token){
		this.token = token;
	}
	
	public String getToken(){
		return token;
	}
	
	public ServiceACLItem(String method,String domain,String ipAddress){
		StringBuffer sb = new StringBuffer();
		if(!StringUtils.isEmpty(method)){
			sb.append("method=").append(method);
		}
		else{
			sb.append("method=*");
		}
		if(!StringUtils.isEmpty(domain)){
			sb.append("&domain=").append(domain);
		}
		else{
			sb.append("&domain=*");
		}
		if(!StringUtils.isEmpty(ipAddress)){
			sb.append("&ip=").append(ipAddress);
		}
		else{
			sb.append("&ip=*");
		}
		token = sb.toString();
	}
	
	@Override
	public int hashCode(){
		return token.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(hashCode() == o.hashCode()){
			return true;
		}
		return false;
	}
}
