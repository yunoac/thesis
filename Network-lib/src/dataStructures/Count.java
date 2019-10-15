package dataStructures;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Count<K> {

	private HashMap<K, Integer> M;

	public Count() {
		M = new HashMap<>();
	}
	
	public Count(K[] a) {
		M = new HashMap<>();
		for(K k : a) add(k);
	}
	
	public int count(K k) {
		if(!M.containsKey(k)) return 0;
		return M.get(k);
	}
	
	public void change(K k, int delta) {
		int count = count(k);
		if(count + delta <= 0) {
			if(M.containsKey(k)) {
				M.remove(k);
			}
		} else {
			M.put(k, count + delta);			
		}
	}
	
	public void add(K k) {
		change(k, 1);
	}
	
	public void remove(K k) {
		change(k, -1);
	}
	
	public void set(K k, int count) {
		if(count == 0 && M.containsKey(k)) {
			M.remove(k);
		} else if(count > 0) {
			M.put(k, count);			
		}
	}
	
	public boolean contains(K k) {
		return count(k) > 0;
	}
	
	public int[] getCounts() {
		int[] counts = new int[M.size()];
		int i = 0;
		for(Entry<K, Integer> e : M.entrySet()) {
			counts[i++] = e.getValue();
		}
		return counts;
	}
	
	public Pair<K, Integer> mostCommon() {
		K maxk = null;
		int max = 0;
		for(K k : M.keySet()) {
			int c = M.get(k);
			if(c > max) {
				max = c;
				maxk = k;
			}
		}
		return new Pair<>(maxk, max);
	}
	
	

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int i = 0;
		Set<Entry<K, Integer>> S = M.entrySet();
		for(Entry<K, Integer> e : S) {
			sb.append(e.getKey().toString() + ": " + e.getValue());
			if(i < S.size() - 1) sb.append(", ");
			i++;
		}
		sb.append("]");
		return sb.toString();
	}
	
}
