package dataStructures;

import graph.Graph;

public class Edge implements Comparable<Edge> {
	
	private String label;
	private int orig, dest, index;
	private Graph g;
	public boolean active;
	private Edge reverse;
	
	public Edge(int orig, int dest) {
		this.orig = orig;
		this.dest = dest;
		this.index = -1;
		active = true;
	}
	
	public Edge(int orig, int dest, String label) {
		this.orig = orig;
		this.dest = dest;
		this.index = -1;
		this.label = label;
		active = true;
	}
	
	public Edge(int orig, int dest, int index) {
		this.orig = orig;
		this.dest = dest;
		this.index = index;
		active = true;
	}
	
	public Edge(int orig, int dest, int index, String label) {
		this.orig = orig;
		this.dest = dest;
		this.index = index;
		this.label = label;
		active = true;
	}
	
	public void setReverse(Edge reverse) {
		this.reverse = reverse;
		reverse.reverse = this;
	}
	
	public Edge getReverse() {
		return reverse;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setGraph(Graph g) {
		this.g = g;
	}
	
	public Edge deepCopy() {
		return new Edge(orig, dest, index);
	}
	
	public int orig() {
		return orig;
	}
	
	public int dest() {
		return dest;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		return toIndexString();
	}
	
	public String toIndexString() {
		return String.format("(%d, %d) %d", orig, dest, index);	
	}
	
	public String toLabelString() {
		if(g != null) {
			return String.format("(%s, %s)", g.getNodeLabel(orig), g.getNodeLabel(dest));
		}
		return String.format("(%d, %d)", orig, dest);
	}

	public int compareTo(Edge other) {
		int dindex = index - other.index;
		if(dindex == 0) {
			int dorig = orig - other.orig;
			if(dorig ==0) return dest - other.dest;
			return dorig;
		}
		return dindex;
	}

}
