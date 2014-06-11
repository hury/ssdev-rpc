package ctd.net.rpc.logger.service;

import java.util.List;

import ctd.net.rpc.logger.invoke.InvokeLog;
import ctd.net.rpc.logger.statistics.StatisticsLog;

public interface LoggerService {
	public void writeInvokeLogs(List<InvokeLog> logs);
	public void writeStatisticsLogs(List<StatisticsLog> logs);
}
