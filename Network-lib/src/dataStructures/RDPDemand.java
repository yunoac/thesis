package dataStructures;

import java.util.HashSet;

public class RDPDemand {

	private int s1, t1, s2, t2;
	
	public RDPDemand(int s1, int t1, int s2, int t2) {
		this.s1 = s1;
		this.t1 = t1;
		this.s2 = s2;
		this.t2 = t2;
	}
	
	public int s1() {
		return s1;
	}
	
	public int s2() {
		return s2;
	}
	
	public int t1() {
		return t1;
	}
	
	public int t2() {
		return t2;
	}
	
	public boolean isSource(int v) {
		return v == s1 || v == s2;
	}
	
	public boolean isDest(int v) {
		return v == t1 || v == t2;
	}
	
	public int getS(int i) {
		if(i == 0) return s1;
		return s2;
	}
	
	public int getT(int i) {
		if(i == 0) return t1;
		return t2;
	}
	
	public HashSet<Integer> getS() {
		HashSet<Integer> S = new HashSet<>();
		S.add(s1);
		S.add(s2);
		return S;
	}
	
	public HashSet<Integer> getT() {
		HashSet<Integer> T = new HashSet<>();
		T.add(t1);
		T.add(t2);
		return T;
	}
	
	public String toString() {
		return String.format("%d->%d,%d->%d", s1, t1, s2, t2);
	}
	
}
