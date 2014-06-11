package ctd.net.rpc.logger.invoke;

import java.util.Date;

import ctd.net.rpc.config.ProviderUrlConfig;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.logger.LoggerManager;
import ctd.spring.AppDomainContext;
import ctd.util.exception.CodedBaseException;

public class InvokeLogBuilder {
	private ServiceConfig service;
	private ProviderUrlConfig url;
	private String methodDesc;
	private long startLong;
	private long timecost;
	private Date requestDt;
	private Date responseDt;
	private Object result;
	private Throwable t;
	private Object[] parameters;
	private int retryCount;
	private int outboundBytes;
	private int inboundBytes;

	public void setService(ServiceConfig service){
		this.service = service;
		setStartTime();
	}
	
	public void setMethodDesc(String methodDesc){
		this.methodDesc = methodDesc;
	}
	
	public void setParameters(Object[] parameters){
		this.parameters = parameters;
	}
	
	public void setPrivoderUrl(ProviderUrlConfig url){
		this.url = url;
	}
	
	private void setStartTime(){
		startLong = System.nanoTime();
		requestDt = new Date();
	}
	
	private void setEndTime(){
		long endLong = System.nanoTime();
		timecost = Math.abs(endLong - startLong) / 1000000;
		responseDt = new Date();
	}
	
	public void exception(Throwable t){
		this.t = t;
		setEndTime();
	}
	
	public void success(Object result){
		this.result = result;
		setEndTime();
	}
	
	public boolean hasException(){
		return t == null;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	
	public int getOutboundBytes() {
		return outboundBytes;
	}

	public void setOutboundBytes(int outboundBytes) {
		this.outboundBytes = outboundBytes;
	}

	public int getInboundBytes() {
		return inboundBytes;
	}

	public void setInboundBytes(int inboundBytes) {
		this.inboundBytes = inboundBytes;
	}

	public void writeLog(){
		int logLevel = service.getProperty("logLevel", int.class, 1);
		if(!LoggerManager.instance().loggerServiceEnabled() || logLevel == InvokeLogLevel.NONE){
			return;
		}
		InvokeLog log = new InvokeLog();
		log.setClientAddress(AppDomainContext.getHost());
		log.setSourceDomain(AppDomainContext.getName());
		log.setDestDomain(service.getAppDomain());
		log.setBeanName(service.getId());
		log.setMethodDesc(methodDesc);
		log.setRequestDt(requestDt);
		log.setResponseDt(responseDt);
		log.setTimeCost(timecost);
		log.setLogLevel(logLevel);
		if(url != null){
			log.setRemoteAddress(url.getHost());
			log.setProtocol(url.getProtocol());
		}
		if(t != null){
			if(t instanceof CodedBaseException){
				log.setResultCode(((CodedBaseException)t).getCode());
			}
			else{
				log.setResultCode(500);
			}
			log.setExceptionMessage(t.getMessage());
		}
		if(logLevel == InvokeLogLevel.ALL){
			log.setParameters(parameters);
			log.setResult(result);
		}
		LoggerManager.instance().log(log);
	}

}
