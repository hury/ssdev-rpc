package ctd.net.rpc.logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ctd.net.rpc.logger.invoke.InvokeLog;
import ctd.net.rpc.logger.invoke.InvokeLogSender;
import ctd.net.rpc.logger.service.LoggerService;
import ctd.net.rpc.logger.statistics.StatisicsManager;
import ctd.net.rpc.logger.statistics.StatisticsLogSender;
import ctd.net.rpc.logger.support.LogDispatcher;
import ctd.net.rpc.logger.support.LogSender;

public class LoggerManager implements LoggerWorkMonitor{
	private static LoggerManager instance;
	private LoggerService loggerService;
	private LogDispatcher dispatcher = new LogDispatcher();
	private StatisicsManager statManager = new StatisicsManager();
	private LogSender<?> invokeLogSender = new InvokeLogSender();
	private LogSender<?> statLogSender = new StatisticsLogSender();
	
	private ExecutorService exec;
	private volatile boolean running;
	
	private int sendRetryTimes = 3;
	private int sendRetryDelayTime =5;
	private int sendCheckDelayTime = 15;
	private int sendBatchSize = 50;
	private int sendMaxStayTime = 30;
	private int statCheckDelayTime = 30;
	
	
	public LoggerManager(){
		instance = this;
	}
	
	public static LoggerManager instance(){
		if(instance == null){
			instance = new LoggerManager();
		}
		return instance;
	}
	
	public void log(InvokeLog log){
		if(loggerServiceEnabled()){
			dispatcher.log(log);
		}
	}
	
	private void initLogSenderEnv(LogSender<?> sender){
		sender.setDelayCheckTime(sendCheckDelayTime);
		sender.setMaxStayTime(sendMaxStayTime*1000);
		sender.setRetryTimes(sendRetryTimes);
		sender.setRetryDelayTime(sendRetryDelayTime);
		sender.setSendBatchSize(sendBatchSize);
	}
	
	public void startWork(){
		
		if(exec != null){
			shutdown();
		}
		
		running = true;
		exec = Executors.newFixedThreadPool(4);
		LoggerWorker[] works = new LoggerWorker[4];
	
		dispatcher.setMonitor(this);
		dispatcher.startWork();
		works[0] = dispatcher;
		
		initLogSenderEnv(invokeLogSender);
		invokeLogSender.setMonitor(this);
		invokeLogSender.setLoggerService(loggerService);
		invokeLogSender.startWork();
		works[1] = invokeLogSender;
		
		initLogSenderEnv(statLogSender);
		statLogSender.setMonitor(this);
		statLogSender.setLoggerService(loggerService);
		statLogSender.startWork();
		works[2] =  statLogSender;
		
		statManager.setMonitor(this);
		statManager.setCheckDelayTime(statCheckDelayTime);
		statManager.startWork();
		works[3] = statManager;
		
		for(LoggerWorker work : works){
			exec.submit(work);
		}
		
	}
	
	public void shutdown(){
		running = false;
		dispatcher.shutdown();
		invokeLogSender.shutdown();
		statLogSender.shutdown();
		if(exec != null && !exec.isTerminated()){
			exec.shutdownNow();
		}
	}
	
	public LoggerService getLoggerService() {
		return loggerService;
	}

	public void setLoggerService(LoggerService loggerService) {
		this.loggerService = loggerService;
	}
	
	public boolean loggerServiceEnabled(){
		return loggerService != null;
	}

	@Override
	public void onInvokeLogSenderExit() {
		if(!running || exec ==null || exec.isTerminated()){
			return;
		}
		if(invokeLogSender.isShutdown()){
			invokeLogSender.startWork();
			exec.submit(invokeLogSender);
		}
		
	}

	@Override
	public void onStatisticsLogSenderExit() {
		if(!running || exec ==null || exec.isTerminated()){
			return;
		}
		if(statLogSender.isShutdown()){
			statLogSender.startWork();
			exec.submit(statLogSender);
		}
	}

	@Override
	public void onDispatherExit() {
		if(!running || exec ==null || exec.isTerminated()){
			return;
		}
		if(dispatcher.isShutdown()){
			dispatcher.startWork();
			exec.submit(dispatcher);
		}
	}

	@Override
	public void onStatisicsManagerExit() {
		if(!running || exec ==null || exec.isTerminated()){
			return;
		}
		if(statManager.isShutdown()){
			statManager.startWork();
			exec.submit(statManager);
		}
		
	}
	
	public void setSendRetryTimes(int sendRetryTimes) {
		this.sendRetryTimes = sendRetryTimes;
	}

	public void setSendRetryDelayTime(int sendRetryDelayTime) {
		this.sendRetryDelayTime = sendRetryDelayTime;
	}

	public void setSendCheckDelayTime(int sendCheckDelayTime) {
		this.sendCheckDelayTime = sendCheckDelayTime;
	}

	public void setSendBatchSize(int sendBatchSize) {
		this.sendBatchSize = sendBatchSize;
	}

	public void setSendMaxStayTime(int sendMaxStayTime) {
		this.sendMaxStayTime = sendMaxStayTime;
	}

	public void setStatCheckDelayTime(int statCheckDelayTime) {
		this.statCheckDelayTime = statCheckDelayTime;
	}
}
