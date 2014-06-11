package ctd.net.rpc.logger.statistics;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.net.rpc.logger.LoggerWorker;
import ctd.util.lock.KeyEntityLockManager;

public class StatisicsManager extends LoggerWorker {
	private static final Logger logger = LoggerFactory.getLogger(StatisicsManager.class);
	private static final ConcurrentHashMap<String,StatisicsLogBuilder> statCenter = new ConcurrentHashMap<String,StatisicsLogBuilder>();
	private static final KeyEntityLockManager lockManager = new KeyEntityLockManager();
	private int delay = 15;
	
	public static void stat(String beanName,String methodDesc,String protocol,String remoteAddress,boolean success,long timeCost){
		String id = StatisicsLogBuilder.buildId(beanName, methodDesc, protocol, remoteAddress);
		StatisicsLogBuilder log = null;
		
		try{
			lockManager.lock(id);
			if(statCenter.containsKey(id)){
				log = statCenter.get(id);
			}
			else{
				log = new StatisicsLogBuilder(beanName, methodDesc, protocol, remoteAddress);
				log.setId(id);
				statCenter.put(id, log);
			}
			if(success)
				log.increaseSuccesses(timeCost);
			else
				log.increaseFailures(timeCost);
		}
		finally{
			lockManager.unlock(id);
		}
	}

	@Override
	public void run() {
		logger.info("logger[StatisicsManager] startting.");
		while(running && !Thread.interrupted()){
			Collection<StatisicsLogBuilder> logBuilds =  statCenter.values();
			for(StatisicsLogBuilder builder : logBuilds){
				String id = builder.getId();
				try{
					lockManager.lock(id);
					if(builder.hasData()){
						StatisticsLogSender.putToSendQueue(builder.build());
						builder.reset();
					}
				}
				finally{
					lockManager.unlock(id);
				}
			}
			try {
				TimeUnit.SECONDS.sleep(delay);
			} 
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		if(monitor != null){
			logger.info("logger[StatisicsManager] thread[{}] exit.",Thread.currentThread().getName());
			monitor.onStatisicsManagerExit();
		}
	}
	
	public void setCheckDelayTime(int delay){
		this.delay = delay;
	}
}
