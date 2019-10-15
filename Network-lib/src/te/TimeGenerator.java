package te;

import java.util.ArrayList;
import java.util.Arrays;

import utils.ArrayExt;

public class TimeGenerator {
	
	public static void main(String[] args) {
		int n = 100;
		int[] times = generateTimeSteps(n, 2);
		System.out.println(Arrays.toString(times));
		System.out.println(n / (double)ArrayExt.max(times));
	}
	
	/*
	 * Generates timestamps and durations for a set of demands such that:
	 * 
	 * 1. the frequency demands is close to 1 / demandsFrequency
	 */
	public static void generateDemandStartTimes(ArrayList<Demand> demands, int demandsFrequency) {
		int[] timestamps = generateTimeSteps(demands.size(), demandsFrequency);
		for(int i = 0; i < timestamps.length; i++) {
			demands.get(i).setTimestampDuration(timestamps[i], 0);
		}
	}
	
	public static int[] generateTimeSteps(int n, int demandsFrequency) {
		int[] time = new int[n];
		time[0] = (int)Math.round(nextTime(demandsFrequency));
		for(int i = 1; i < n; i++) {
			time[i] = time[i - 1] + (int)Math.round(nextTime(demandsFrequency));
		}
		return time;
	}
	
	public static double nextTime(int demandsPerTimestamp) {
		return -Math.log(1 - Math.random()) * demandsPerTimestamp;
	}

}
