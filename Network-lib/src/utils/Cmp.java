package utils;

public class Cmp {

	public static double eps = 1e-8;
	
	public static boolean eq(double x, double y) {
		if(x == Double.POSITIVE_INFINITY && y == Double.POSITIVE_INFINITY) return true;
		return Math.abs(x - y) <= eps;
	}
	
	public static boolean geq(double x, double y) {
		return x >= y - eps;
	}
	
	public static boolean gr(double x, double y) {
		return x > y + eps;
	}
	
	public static boolean le(double x, double y) {
		return x < y - eps;
	}
	
	public static boolean leq(double x, double y) {
		return x <= y + eps;
	}
	
	public static boolean eq(double[] a, double[] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(!eq(a[i], b[i])) return false;
		}
		return true;
	}
	
	public static boolean eq(int[][] a, int[][] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(a[i].length != b[i].length) return false;
			for(int j = 0; j < a[i].length; j++) {
				if(a[i][j] != b[i][j]) return false;
			}
		}
		return true;
	}
	
}
