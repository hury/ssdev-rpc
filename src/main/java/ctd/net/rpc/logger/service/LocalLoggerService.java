package ctd.net.rpc.logger.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.net.rpc.logger.invoke.InvokeLog;
import ctd.net.rpc.logger.invoke.InvokeLogLevel;
import ctd.net.rpc.logger.statistics.StatisticsLog;
import ctd.util.converter.ConversionUtils;

public class LocalLoggerService implements LoggerService {
	protected static final Logger logger = LoggerFactory.getLogger(LocalLoggerService.class);
	
	@Override
	public void writeInvokeLogs(List<InvokeLog> logs) {
		try{
			for(InvokeLog o : logs){
				logger.info("service[{}]@[{}]",o.getBeanName(),o.getRemoteAddress());
				logger.info("+--method[{}]",o.getMethodDesc());
				logger.info("+--requestDt[{}]",o.getRequestDt());
				logger.info("+--responseDt[{}]",o.getResponseDt());
				logger.info("+--resultCode[{}]",o.getResultCode());
				if(o.getLogLevel() > InvokeLogLevel.STAT){
					Object[] parameters = o.getParameters();
					if(parameters != null){
						for(int i = 0; i < parameters.length; i ++){
							logger.info("+--parameter[{}]={}",i,ConversionUtils.convert(parameters[i], String.class));
						}
					}
					String errText = o.getExceptionMessage();
					if(StringUtils.isEmpty(errText)){
						logger.info("+--exceptionMessage[{}]",errText);
					}
					else{
						logger.info("+--result[{}]",ConversionUtils.convert(o.getResult(),String.class));
					}
				}
			}
		}
		catch(Exception e){
			logger.error("output logs to local failed:",e);
		}
	}

	@Override
	public void writeStatisticsLogs(List<StatisticsLog> logs) {
		try{
			for(StatisticsLog o : logs){
				logger.info("service[{}]@[{}]",o.getBeanName(),o.getRemoteAddress());
				logger.info("+--method[{}]",o.getMethodDesc());
				logger.info("+--startDt[{}],endDt[{}]",o.getStatStart(),o.getStatEnd());
				logger.info("+--Successes[{}]",o.getSuccesses());
				logger.info("+--Failures[{}]",o.getFailures());
				logger.info("+--maxTimeCost[{}]",o.getMaxTimeCost());
				logger.info("+--minTimeCost[{}]",o.getMinTimeCost());
				logger.info("+--lastTimeCost[{}]",o.getLastTimeCost());
				logger.info("+--AvgTimeCost[{}]",o.getAvgTimeCost());
			}
		}
		catch(Exception e){
			logger.error("output logs to local failed:",e);
		}
	}

}
