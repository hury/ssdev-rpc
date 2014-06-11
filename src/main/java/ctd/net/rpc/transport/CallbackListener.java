package ctd.net.rpc.transport;

import ctd.net.rpc.Result;

public interface CallbackListener {
	void onCallback(Result result);
}
