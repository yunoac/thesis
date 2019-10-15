package tests;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Bitset;
import graph.Graph;
import sr.SrReach;

public class Test_Biset {
	
	@Test
	public void test1() {
		Bitset b = new Bitset();
		b.set(1);
		b.set(5);
		b.set(9);
		b.set(10001);
		LinkedList<Integer> L = new LinkedList<>();
		for(int x : b) {
			L.add(x);
		}
		assertTrue(L.toString().equals("[1, 5, 9, 10001]"));
	}

	
	@Test
	public void test2() {
		Bitset b = new Bitset();
		LinkedList<Integer> L = new LinkedList<>();
		for(int x : b) {
			L.add(x);
		}
		assertTrue(L.toString().equals("[]"));
	}
	
	@Test
	public void test3() {
		Bitset b = new Bitset();
		b.set(1000000001);
		LinkedList<Integer> L = new LinkedList<>();
		for(int x : b) {
			L.add(x);
		}
		assertTrue(L.toString().equals("[1000000001]"));
	}
	
	@Test
	public void test4() {
		Bitset b = new Bitset();
		b.set(1);
		b.set(5);
		b.set(9);
		b.set(10001);
		b.clear(9);
		LinkedList<Integer> L = new LinkedList<>();
		for(int x : b) {
			L.add(x);
		}
		assertTrue(L.toString().equals("[1, 5, 10001]"));
	}
	
	@Test
	public void test5() {
		Bitset b = new Bitset();
		b.set(0);
		b.set(1);
		b.set(2);
		b.set(3);
		b.set(5);
		b.set(4);
		LinkedList<Integer> L = new LinkedList<>();
		for(int x : b) {
			L.add(x);
		}
		assertTrue(L.toString().equals("[0, 1, 2, 3, 4, 5]"));
	}
}
