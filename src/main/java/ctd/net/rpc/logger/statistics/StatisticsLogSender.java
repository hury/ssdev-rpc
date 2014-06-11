package ctd.net.rpc.logger.statistics;


import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


import ctd.net.rpc.logger.support.LogSender;


public class StatisticsLogSender extends LogSender<StatisticsLog>{
	
	private static final Queue<StatisticsLog> sendQueue = new ConcurrentLinkedQueue<StatisticsLog>();
	
	
	public static void putToSendQueue(StatisticsLog log){
		sendQueue.offer(log);
	}
	
	@Override
	protected StatisticsLog getNextLog() {
		StatisticsLog log= sendQueue.poll();
		return log;
	}

	@Override
	protected void outputLogs(List<StatisticsLog> ls) {
		loggerService.writeStatisticsLogs(ls);
	}

	@Override
	protected void outputLogs2LocalLogger(List<StatisticsLog> ls) {
		localLoggerService.writeStatisticsLogs(ls);
	}
	
	@Override
	protected void beforeStart() {
		String name = Thread.currentThread().getName();
		logger.info("logger[StatisticsLogSender] startting",name);
	}

	@Override
	protected void beforeExit() {
		String name = Thread.currentThread().getName();
		logger.info("logger[StatisticsLogSender]",name);
		super.beforeExit();
	}

	
}
