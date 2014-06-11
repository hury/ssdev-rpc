package ctd.net.rpc.logger.statistics;

import java.util.Date;

import ctd.net.rpc.logger.Log;

public class StatisticsLog extends Log{

	private Date statStart;
	private Date statEnd;
	private int successes;
	private int failures;
	private long maxTimeCost;
	private long minTimeCost;
	private double avgTimeCost;
	private long lastTimeCost;
	
	public Date getStatStart() {
		return statStart;
	}
	
	public void setStatStart(Date statStart) {
		this.statStart = statStart;
	}
	
	public Date getStatEnd() {
		return statEnd;
	}
	
	public void setStatEnd(Date statEnd) {
		this.statEnd = statEnd;
	}
	
	public long getMaxTimeCost() {
		return maxTimeCost;
	}
	
	public void setMaxTimeCost(long maxTimeCost) {
		this.maxTimeCost = maxTimeCost;
	}
	
	public int getSuccesses() {
		return successes;
	}
	
	public void setSuccesses(int successes) {
		this.successes = successes;
	}
	
	public int getFailures() {
		return failures;
	}
	
	public void setFailures(int failures) {
		this.failures = failures;
	}
	
	public long getTotalCount(){
		return successes + failures;
	}
	
	public long getMinTimeCost() {
		return minTimeCost;
	}
	
	public void setMinTimeCost(long minTimeCost) {
		this.minTimeCost = minTimeCost;
	}
	
	public double getAvgTimeCost() {
		return avgTimeCost;
	}
	
	public void setAvgTimeCost(double avgTimeCost) {
		this.avgTimeCost = avgTimeCost;
	}
	
	public long getLastTimeCost() {
		return lastTimeCost;
	}
	
	public void setLastTimeCost(long lastTimeCost) {
		this.lastTimeCost = lastTimeCost;
	}
}
