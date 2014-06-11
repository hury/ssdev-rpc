package ctd.net.rpc.logger.invoke;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import ctd.net.rpc.logger.support.LogSender;

public class InvokeLogSender extends LogSender<InvokeLog>{
	private static final Queue<InvokeLog> sendQueue = new ConcurrentLinkedQueue<InvokeLog>();
	
	public static void putToSendQueue(InvokeLog log){
		sendQueue.offer(log);
	}

	@Override
	protected InvokeLog getNextLog() {
		return sendQueue.poll();
	}

	@Override
	protected void outputLogs(List<InvokeLog> ls) {
		loggerService.writeInvokeLogs(ls);
	}

	@Override
	protected void outputLogs2LocalLogger(List<InvokeLog> ls) {
		localLoggerService.writeInvokeLogs(ls);
	}

	@Override
	protected void beforeStart() {
		logger.info("logger[invokeLogSender] startting.");
	}

	@Override
	protected void beforeExit() {
		logger.info("invokeLogSender stopped.");
		super.beforeExit();
	}
	
}
