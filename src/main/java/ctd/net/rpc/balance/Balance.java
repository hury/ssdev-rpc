package ctd.net.rpc.balance;

import java.util.List;

import ctd.net.rpc.config.ProviderUrlConfig;

public interface Balance {
	ProviderUrlConfig select(List<ProviderUrlConfig> ls);
}
