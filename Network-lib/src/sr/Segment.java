package sr;

import dataStructures.Edge;
import graph.Graph;

public class Segment {

	private int s1, s2;
	private int edgeIndex;
	private Graph g;
	private Edge e;

	public Segment(int x) {
		this.s1 = this.s2 = x;
		edgeIndex = -1;
	}

	public Segment(Edge e) {
		this.s1 = e.orig();
		this.s2 = e.dest();
		this.e = e;
		edgeIndex = e.getIndex();
	}

	public int getIndex() {
		return edgeIndex;
	}
	
	public Edge getEdge() {
		return e;
	}

	public void setGraph(Graph g) {
		this.g = g;
	}

	public int s1() {
		return s1;
	}

	public int s2() {
		return s2;
	}

	public boolean isAdj() {
		return s1 != s2;
	}

	public boolean isNode() {
		return s1 == s2;
	}
	
	public boolean equals(Object other) {
		if(other instanceof Segment) {
			Segment o = (Segment)other;
			if(this.isAdj() != o.isAdj()) return false;
			if(this.isNode() != o.isNode()) return false;
			if(this.isNode()) return s1 == o.s1;
			return this.getEdge().getIndex() == o.getEdge().getIndex();
		}
		return false;
	}

	public String toString() {
		if(g == null) {
			if(isNode()) return s1 + "";
			return String.format("(%d, %d)", s1, s2);
		}
		if(isNode()) return g.getNodeLabel(s1);
		return String.format("(%s, %s)", g.getNodeLabel(s1), g.getNodeLabel(s2));
	}

	public String toIndexString() {
		if(isNode()) return s1 + "";
		return String.format("(%d, %d)", s1, s2);
	}

	public int getSegmentCost() {
		return isAdj() ? 2 : 1;
	}

}
