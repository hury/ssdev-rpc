package ctd.net.rpc.transport.support.stat;



import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

public class TrafficCounter {
	private final AtomicLong currentWrittenBytes = new AtomicLong();
    private final AtomicLong currentReadBytes = new AtomicLong();

  
    private final AtomicLong cumulativeWrittenBytes = new AtomicLong();
    private final AtomicLong cumulativeReadBytes = new AtomicLong();

    private long lastCumulativeTime;
    
    private long lastWriteThroughput;
    private long lastReadThroughput;
    
    private final AtomicLong lastTime = new AtomicLong();
    private long lastWrittenBytes;
    private long lastReadBytes;

    final AtomicLong checkInterval = new AtomicLong(10000);
   
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    private volatile ScheduledFuture<?> scheduledFuture;
    
    

}
