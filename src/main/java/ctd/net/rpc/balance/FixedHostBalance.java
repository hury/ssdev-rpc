package ctd.net.rpc.balance;

import java.util.List;

import ctd.net.rpc.config.ProviderUrlConfig;

public class FixedHostBalance implements Balance {
	private String host;
	
	public FixedHostBalance(String host){
		this.host = host;
	}
	
	@Override
	public ProviderUrlConfig select(List<ProviderUrlConfig> ls) {
		int max= ls.size();
		if(max == 0){
			return null;
		}
		for(ProviderUrlConfig pUrl : ls){
			if(pUrl.getHost().equals(host)){
				return pUrl;
			}
		}
		return null;
	}

}
