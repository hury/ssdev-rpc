package ctd.net.rpc.balance;



import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ctd.net.rpc.config.ProviderUrlConfig;

public class RandomBalance implements Balance {

	@Override
	public ProviderUrlConfig select(List<ProviderUrlConfig> ls) {
		int max= ls.size();
		if(max == 0){
			return null;
		}
		if(max == 1){
			return ls.get(0);
		}
		int index = ThreadLocalRandom.current().nextInt(max);
		return ls.get(index);
	}

}
