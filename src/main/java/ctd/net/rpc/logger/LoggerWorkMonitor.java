package ctd.net.rpc.logger;

public interface LoggerWorkMonitor {
	public void onInvokeLogSenderExit();
	public void onStatisticsLogSenderExit();
	public void onDispatherExit();
	public void onStatisicsManagerExit();
}
