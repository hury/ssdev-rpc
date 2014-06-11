package ctd.net.rpc.logger.statistics;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.thymeleaf.util.StringUtils;

import ctd.spring.AppDomainContext;

@SuppressWarnings("unused")
public class StatisicsLogBuilder {

	private String id;
	private Date statStart;
	
	private String beanName;
	private String methodDesc;
	private String protocol;
	private String remoteAddress;
	
	private int successes;
	private int failures;
	private long maxTimeCost;
	private long minTimeCost;
	private double avgTimeCost;
	private long lastTimeCost;
	
	public StatisicsLogBuilder(String beanName,String methodDesc,String protocol,String remoteAddress){
		statStart = new Date();
		this.beanName = beanName;
		this.methodDesc = methodDesc;
		this.protocol = protocol;
		this.remoteAddress = remoteAddress;
	}
	
	public static String buildId(String beanName,String methodDesc,String protocol,String remoteAddress){
		StringBuilder sb = new StringBuilder(beanName);
		sb.append(methodDesc);
		if(protocol != null){
			sb.append(protocol);
		}
		if(remoteAddress != null){
			sb.append(remoteAddress);
		}
		return sb.toString();
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		if(id == null){
			id = buildId(beanName,methodDesc,protocol,remoteAddress);
		}
		return id;
	}

	public void increaseSuccesses(long timeCost){
		successes = successes + 1;
		caluTimeCost(timeCost);
	}
	
	public void increaseFailures(long timeCost){
		failures = failures + 1;
		caluTimeCost(timeCost);
	}
	
	public boolean hasData(){
		return (successes + failures) > 0;
	}
	
	private void caluTimeCost(long timeCost){
		maxTimeCost = Math.max(maxTimeCost, timeCost);
		minTimeCost = Math.min(minTimeCost, timeCost);
		int total = successes + failures;
		if(total > 1){
			minTimeCost = Math.min(minTimeCost, timeCost);
		}
		else{
			minTimeCost = timeCost;
		}
		avgTimeCost = ((total-1) * avgTimeCost + timeCost)/total; 
		lastTimeCost = timeCost;
	}
	
	public void reset(){
		successes = 0;
		failures = 0;
		maxTimeCost = 0l;
		minTimeCost = 0l;
		lastTimeCost = 0l;
		avgTimeCost = 0d;
		statStart = new Date();
	}
	
	public StatisticsLog build(){
		StatisticsLog log = new StatisticsLog();
		log.setStatStart(statStart);
		log.setStatEnd(new Date());
		log.setBeanName(beanName);
		log.setMethodDesc(methodDesc);
		log.setClientAddress(AppDomainContext.getHost());
		log.setSourceDomain(AppDomainContext.getName());
		log.setRemoteAddress(remoteAddress);
		log.setDestDomain(StringUtils.substringBefore(beanName, "."));
		log.setAvgTimeCost(avgTimeCost);
		log.setLastTimeCost(lastTimeCost);
		log.setMaxTimeCost(maxTimeCost);
		log.setMinTimeCost(minTimeCost);
		log.setSuccesses(successes);
		log.setFailures(failures);
		return log;
	}
}
