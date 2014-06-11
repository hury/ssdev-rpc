package ctd.net.rpc.transport;

public interface Server {
	void setServerUrl(ServerUrl url);
	void start();
	void shutdown();
	void setThreads(int threads);
	void setQueues(int queues);
}
