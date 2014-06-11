package ctd.net.rpc.logger;

public abstract class LoggerWorker implements Runnable{
	protected volatile boolean running;
	protected LoggerWorkMonitor monitor;
	
	public void startWork(){
		running = true;
	}
	
	public void shutdown(){
		running = false;
	}
	
	public void setMonitor(LoggerWorkMonitor monitor){
		this.monitor = monitor;
	}
	
	public boolean isShutdown(){
		return !running;
	}
}
