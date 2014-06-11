package ctd.net.rpc.logger.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.logger.Log;
import ctd.net.rpc.logger.LoggerWorker;
import ctd.net.rpc.logger.service.LocalLoggerService;
import ctd.net.rpc.logger.service.LoggerService;

public abstract class LogSender<T extends Log> extends LoggerWorker{
	protected static final Logger logger = LoggerFactory.getLogger(LogSender.class);
	protected LoggerService loggerService;
	protected LoggerService localLoggerService = new LocalLoggerService();
	private long delayCheckTime = 15;
	private int sendBatchSize = 50;
	private int retryTimes = 3;
	private int retryDelayTime = 5;
	private long maxStayTime = 30000l;
	
	@Override
	public void run() {
		beforeStart();
		List<T> ls = new ArrayList<T>();
		int retryCount = 0;
		long lastSendTime = System.nanoTime();
		while(running && !Thread.interrupted()){
			int size = ls.size();
			long now = System.nanoTime();
			long timeOffset = Math.abs(now - lastSendTime)/1000000;
			if(size >= sendBatchSize || (size > 0 && timeOffset >= maxStayTime)){
				try{
					outputLogs(ls);
					lastSendTime = now;
					ls.clear();
					retryCount = 0;
				}
				catch(Exception e){
					retryCount ++;
					if(RpcException.class.isInstance(e)){
						RpcException re = (RpcException)e;
						if(re.isServiceOffline()){
							logger.warn("loggerService is offline,send list size:" + ls.size());
						}
					}
					if(retryCount < retryTimes){
						try {
							logger.warn("loggerService invoke exception,retry for[{}/{}]times",retryCount,retryTimes);
							TimeUnit.SECONDS.sleep(retryDelayTime);
						}
						catch (InterruptedException e1) {
							Thread.currentThread().interrupt();
							logger.warn("logSender thread[{}] is interrupt and exit.",Thread.currentThread().getName());
						}
					}
					else{
						logger.error("loggerService invoke exception,output logs to local:" + e.getMessage());
						outputLogs2LocalLogger(ls);
						lastSendTime = now;
						ls.clear();
					}
				}
			}
			
			T log = getNextLog();
			if(log != null){
				ls.add(log);
			}	
			else{
				try {
					TimeUnit.SECONDS.sleep(delayCheckTime);
				} 
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.warn("logSender thread[{}] is interrupt and exit.",Thread.currentThread().getName());
				}
			}
		}
		beforeExit();
	}
	
	protected abstract void beforeStart();
	protected void beforeExit(){
		running = false;
		if(monitor != null){
			monitor.onInvokeLogSenderExit();
		}
	}
	protected abstract T getNextLog();
	protected abstract void outputLogs(List<T> ls);
	protected abstract void outputLogs2LocalLogger(List<T> ls);

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public void setRetryDelayTime(int retryDelayTime) {
		this.retryDelayTime = retryDelayTime;
	}

	public void setDelayCheckTime(long delayCheckTime) {
		this.delayCheckTime = delayCheckTime;
	}

	public void setLoggerService(LoggerService loggerService) {
		this.loggerService = loggerService;
	}

	public void setSendBatchSize(int sendBatchSize) {
		this.sendBatchSize = sendBatchSize;
	}
	
	public void setMaxStayTime(long maxStayTime) {
		this.maxStayTime = maxStayTime;
	}

}
