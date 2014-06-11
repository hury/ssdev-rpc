package ctd.net.rpc.transport.support;

import ctd.net.rpc.transport.Server;

public abstract class AbstractServer extends AbstractTransport implements Server {
	protected int threads = Integer.MAX_VALUE;
	protected int queues = 0;
	
	@Override
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	@Override
	public void setQueues(int queues) {
		this.queues = queues;
	}

}
