package ctd.net.rpc.logger.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ctd.net.rpc.logger.LoggerManager;
import ctd.net.rpc.logger.LoggerWorker;
import ctd.net.rpc.logger.invoke.InvokeLog;
import ctd.net.rpc.logger.invoke.InvokeLogLevel;
import ctd.net.rpc.logger.invoke.InvokeLogSender;
import ctd.net.rpc.logger.statistics.StatisicsManager;

public class LogDispatcher extends LoggerWorker{
	private static final Logger logger = LoggerFactory.getLogger(LoggerManager.class);
	private static final BlockingQueue<InvokeLog> logs = new LinkedBlockingQueue<InvokeLog>();
	
	public void log(InvokeLog log){
		logs.add(log);
	}
	
	@Override
	public void run() {
		logger.info("logger[logDispatcher] startting.");
		while(running && !Thread.interrupted()){
			try {
				InvokeLog log = logs.take();
				StatisicsManager.stat(log.getBeanName(), log.getMethodDesc(), log.getProtocol(), log.getRemoteAddress(), log.getExceptionMessage() == null, log.getTimeCost());
				if(log.getLogLevel() > InvokeLogLevel.STAT){
					InvokeLogSender.putToSendQueue(log);
				}
				
			}
			catch (InterruptedException e) {
				logger.warn("take from queue interrupted,LoggerManager exit.");
				Thread.currentThread().interrupt();
			}
			
		}
		if(monitor != null){
			logger.info("logger[logDispatcher] thread[{}] exit.",Thread.currentThread().getName());
			monitor.onDispatherExit();
		}
		
	}
	
}
