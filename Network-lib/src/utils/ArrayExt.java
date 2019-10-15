package utils;

import dataStructures.Pair;

public class ArrayExt {
	
	/*
	 * Return an array b of the same size as a such that b[i] is
	 * true iff a[i] = target
	 */
	public static boolean[] getEqualIndexes(Integer[] a, int target) {
		boolean[] equalIndexes = new boolean[a.length];
		for(int i = 0; i < a.length; i++) {
			equalIndexes[i] = a[i] == target;
		}
		return equalIndexes;
	}
	
	
	public static String toString(double[][] a) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < a.length; i++) {
			for(int j = 0; j < a[i].length; j++) {
				sb.append(String.format("%.3f", a[i][j]));
				if(j < a[i].length - 1) {
					sb.append(" ");					
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static double sum(double[] a) {
		double s = 0;
		for(double x : a) s += x;
		return s;
	}
	
	public static int sum(int[] a) {
		int s = 0;
		for(int x : a) s += x;
		return s;
	}
	
	public static double max(double[] a) {
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < a.length; i++) {
			max = Math.max(max, a[i]);
		}
		return max;
	}
	
	public static double min(double[] a) {
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i < a.length; i++) {
			min = Math.min(min, a[i]);
		}
		return min;
	}
	
	public static int max(int[] a) {
		int max = Integer.MIN_VALUE;
		for(int i = 0; i < a.length; i++) {
			max = Math.max(max, a[i]);
		}
		return max;
	}
	
	
	public static boolean eq(double[] a, double[] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(!Cmp.eq(a[i], b[i])) return false;
		}
		return true;
	}
	
	public static boolean eq(double[][] a, double[][] b) {
		if(a.length != b.length) return false;
		int n = a.length;
		for(int i = 0; i < n; i++) {
			if(a[i].length != b[i].length) return false;
			int m = a[i].length;
			for(int j = 0; j < m; j++) {
				if(!Cmp.eq(a[i][j], b[i][j])) return false;				
			}
		}
		return true;
	}
	
	public static Pair<Integer, Integer> diffIndex(double[][] a, double[][] b) {
		if(a.length != b.length) throw new IllegalArgumentException("matrices should have the same number of rows");
		int n = a.length;
		for(int i = 0; i < n; i++) {
			if(a[i].length != b[i].length) throw new IllegalArgumentException("matrices should have the same number of columns");
			int m = a[i].length;
			for(int j = 0; j < m; j++) {
				if(!Cmp.eq(a[i][j], b[i][j])) return new Pair<>(i, j);				
			}
		}
		return null;
	}
	
	
	
}
