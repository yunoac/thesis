package utils;

import java.util.ArrayList;
import java.util.BitSet;

public class ThreadSolver {
	
	private int progress;
	private BitSet running, done;
	private int maxThreads;
	private ArrayList<? extends Thread> threads;
	
	public ThreadSolver(int maxThreads, ArrayList<? extends Thread> threads) {
		this.maxThreads = maxThreads;
		this.threads = threads;
	}
	
	public void run() {
		run("");
	}
	
	public void run(String id) {
		running = new BitSet();
		done = new BitSet();
		int lastProgress = 0;
		while(done.cardinality() < threads.size()) {
			progress =  (int)(100.0 * done.cardinality() / threads.size());
			if(progress != lastProgress) {
				lastProgress = progress;
				System.out.println(id + " " + progress + "%");
			}
			for(int i = 0; i < threads.size(); i++) {
				if(!threads.get(i).isAlive() && running.cardinality() < maxThreads && !running.get(i) && !done.get(i)) {
					running.set(i);
					threads.get(i).start();
				} else if(!threads.get(i).isAlive() && running.get(i)) {
					done.set(i);
					running.clear(i);
				}
			}
		}
	}

	public boolean finished() {
		return done.cardinality() == threads.size();
	}
	
	public int progress() {
		return progress;
	}
	
}
