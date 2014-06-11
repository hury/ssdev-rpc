package ctd.net.rpc.logger.invoke;

import java.util.Date;

import ctd.net.rpc.logger.Log;

public class InvokeLog extends Log{
	private int resultCode = 200;
	private Date requestDt;
	private Date responseDt;
	private long timeCost;
	private Object[] parameters;
	private Object result;
	private int retryCount;
	private String exceptionMessage;
	private int logLevel;
	
	public int getResultCode() {
		return resultCode;
	}
	
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Date getRequestDt() {
		return requestDt;
	}

	public void setRequestDt(Date requestDt) {
		this.requestDt = requestDt;
	}

	public Date getResponseDt() {
		return responseDt;
	}

	public void setResponseDt(Date responseDt) {
		this.responseDt = responseDt;
	}

	public long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(long timeCost) {
		this.timeCost = timeCost;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}
	
}
