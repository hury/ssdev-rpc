package ctd.test.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class TestThread implements Runnable{
	private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	@Override
	public void run() {
		System.out.println("start");
		while(!Thread.interrupted()){
			try {
				String s = queue.take();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("thread interrupt.");
			}
		}
		System.out.println("stop");
		
	}
	
	public static void main(String[] args) throws InterruptedException{
		ExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		Future<?> f = exec.submit(new TestThread());
		
		
		TimeUnit.SECONDS.sleep(3);
		System.out.println("next to interrupt");
		exec.shutdownNow();
		//f.cancel(false);
		//t.interrupt();
	}
}
