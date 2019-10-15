package monitoring;

import java.util.ArrayList;

public class CGLog {

	private ArrayList<Double> objValue;
	private ArrayList<Long> time;
	private long startTime;
	
	public CGLog() {
		objValue = new ArrayList<>();
		time = new ArrayList<>();
		startTime = System.nanoTime();
	}
	
	public void add(double value) {
		objValue.add(value);
		time.add(System.nanoTime() - startTime);
	}
	
	public double lpBound() {
		return objValue.get(objValue.size() - 1);
	}
	
	public double[] valuesToArray() {
		double[] a = new double[objValue.size()];
		for(int i = 0; i < a.length; i++) {
			a[i] = objValue.get(i);
		}
		return a;
	}
	
	public long[] timesToArray() {
		long[] a = new long[objValue.size()];
		for(int i = 0; i < a.length; i++) {
			a[i] = time.get(i);
		}
		return a;	
	}
	
}
