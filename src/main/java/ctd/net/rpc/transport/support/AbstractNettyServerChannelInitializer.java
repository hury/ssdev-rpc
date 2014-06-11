package ctd.net.rpc.transport.support;


public abstract class AbstractNettyServerChannelInitializer extends AbstractNettyChannelInitializer {
	protected int threads = Integer.MAX_VALUE;
	protected int queues = 0;
	
	
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	
	public void setQueues(int queues) {
		this.queues = queues;
	}

}
