package utils;

import dataStructures.Pair;

public class MyAssert {

	public static void assertTrue(boolean a) {
		if(!a) throw new RuntimeException("assertion error");
	}
	
	public static void assertTrue(boolean a, String msg) {
		if(!a) throw new RuntimeException("assertion error: " + msg);
	}
	
	public static void assertTrue(Pair<Boolean, String> p) {
		if(!p.first()) throw new RuntimeException("assertion error: " + p.second());		
	}
	
	
}
