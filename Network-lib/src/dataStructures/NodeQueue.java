package dataStructures;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

public class NodeQueue implements Iterable<Integer> {

	private Bitset inQueue, visited;
	private LinkedList<Integer> elems;
	
	public NodeQueue() {
		inQueue = new Bitset();
		visited = new Bitset();
		elems = new LinkedList<>();
	}
	
	public void add(int v) {
		if(!inQueue.get(v)) {
			inQueue.set(v);
			visited.set(v);
			elems.addLast(v);
		}
	}
	
	public int poll() {
		int a = elems.removeFirst();
		inQueue.clear(a);
		return a;
	}
	
	public boolean contains(int v) {
		return inQueue.get(v);
	}
	
	public int size() {
		return elems.size();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public boolean visited(int v) {
		return visited.get(v);
	}
	
	public Bitset getVisitedNodes() {
		return visited;
	}

	public Iterator<Integer> iterator() {
		return elems.iterator();
	}
	
	public String toString() {
		return "in: " + inQueue + ", vis:" + visited;
	}
	
}
