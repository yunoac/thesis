package sr;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

import dataStructures.Edge;
import exceptions.IndexedAccessException;
import graph.Graph;
import graph.Graphs;
import graph.Path;

public class SrPath implements Iterable<Segment> {
	
	private LinkedList<Segment> segList;
	private ArrayList<Segment> segArray;
	private double weight;
	private int nbNodeSeg, nbAdjSeg;
	private boolean indexed;
	
	public SrPath() {
		segList = new LinkedList<>();
		segArray = new ArrayList<>();
		indexed = true;
	}
	

	public SrPath(LinkedList<Integer> nodes) {
		segList = new LinkedList<>();
		segArray = new ArrayList<>();
		for(int v : nodes) {
			Segment s = new Segment(v);
			segList.add(s);
			segArray.add(s);
		}
 		indexed = true;
	}
	
	public SrPath(int[] nodes) {
		segList = new LinkedList<>();
		segArray = new ArrayList<>();
		for(int v : nodes) {
			Segment s = new Segment(v);
			segList.add(s);
			segArray.add(s);
		}
 		indexed = true;	
	}
	
	public SrPath copy() {
		SrPath p = new SrPath();
		for(Segment seg : segList) {
			p.add(seg);
		}
		return p;
	}
	
	public double getLat(Graph g, ForwGraphs forw) {
		double lat = 0;
		for(Segment s : segList) {
			if(s.isAdj()) {
				lat += g.getWeight("lat", s.getEdge());
			}
		}
		for(int i = 1; i < size(); i++) {
			lat += forw.getForwLat(get(i - 1).s2(), get(i).s1());
		}
		return lat;
	}
	
	public boolean equals(Object other) {
		if(other instanceof SrPath) {
			SrPath o = (SrPath)other;
			if(size() != o.size()) return false;
			for(int i = 0; i < size(); i++) {
				if(!get(i).equals(o.get(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public int nbAdjSeg() {
		return nbAdjSeg;
	}
	
	public int nbNodeSeg() {
		return nbNodeSeg;
	}

	
	/*
	 * Create an sr-path from s to t using a parent matrix.
	 */
	public SrPath(Segment[][] parent, int orig, int dest, int nbSeg) {
		buildPath(parent, orig, dest, nbSeg);
	}
	
	/*
	 * Create an sr-path from s to t using a parent matrix.
	 */
	public SrPath(Segment[][] parent, int orig, int dest) {
		buildPath(parent, orig, dest, parent.length - 1);
	}
	
	private void buildPath(Segment[][] parent, int orig, int dest, int nbSeg) {
		segList = new LinkedList<>();
		segArray = new ArrayList<>();
		Segment cur = parent[nbSeg][dest];
		while(cur != null) {
			segList.addFirst(cur);
			//nbSeg -= cur.isAdj() ? 2 : 1;
			nbSeg -= 1;
			cur = parent[nbSeg][cur.s1()];
		}
		if(segList.isEmpty() || segList.getFirst().s1() != orig) segList.addFirst(new Segment(orig));
		if(segList.getLast().s2() != dest) segList.addLast(new Segment(dest));
		indexPath();
	}
	
	/*
	 * This method should be called after you finish add all the segments
	 * if you which to access elements by index. It is only necessary if you
	 * used the addFirst method for creating your path.
	 * 
	 * If you modify the path using addLast there is no need to re-index the path.
	 */
	public void indexPath() {
		segArray.clear();
		nbNodeSeg = nbAdjSeg = 0;
		for(Segment s : segList) {
			segArray.add(s);
			if(s.isNode()) nbNodeSeg += 1;
			if(s.isAdj()) nbAdjSeg += 1;
		}
		indexed = true;
	}
	
	public BitSet getEdgeSet(Graph g) {
		BitSet edgeSet = new BitSet();
		for(Segment seg : segList) {
			if(seg.isAdj()) edgeSet.set(seg.getEdge().getIndex());
		}
		for(int i = 1; i < segArray.size(); i++) {
			int prev = get(i - 1).s2();
			int next = get(i).s1();
			Graph dag = Graphs.shortestPathDag(g, prev, next, "igp");
			for(Edge e : dag.getEdgesByIndex()) {
				edgeSet.set(e.getIndex());
			}
		}
		return edgeSet;
	}
	
	public BitSet getEdgeSet(Graph g, ForwGraphs forw) {
		BitSet edgeSet = new BitSet();
		for(Segment seg : segList) {
			if(seg.isAdj()) edgeSet.set(seg.getEdge().getIndex());
		}
		for(int i = 1; i < segArray.size(); i++) {
			int prev = get(i - 1).s2();
			int next = get(i).s1();
			Graph dag = forw.getForwGraphSubgraph(prev, next);
			for(Edge e : dag.getEdgesByIndex()) {
				edgeSet.set(e.getIndex());
			}
		}
		return edgeSet;
	}
	
	public ArrayList<Edge> getEdges(Graph g, ForwGraphs forw) {
		BitSet edgeSet = getEdgeSet(g, forw);
		ArrayList<Edge> edges = new ArrayList<>();
		for(Edge e : g.getEdgesByIndex()) {
			if(edgeSet.get(e.getIndex())) {
				edges.add(e);
			}
		}
		return edges;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public int orig() {
		return segList.getFirst().s1();
	}
	
	public int dest() {
		return segList.getLast().s2();
	}
	
	public int getSegmentCost() {
		return nbNodeSeg + 2 * nbAdjSeg;
	}
	
	public int segCost(int adjCost) {
		return nbNodeSeg + adjCost * nbAdjSeg;
	}
	
	public int size() {
		return segList.size();
	}

	public void add(int v) {
		addLast(new Segment(v));
	}
	
	public void add(Edge e) {
		addLast(new Segment(e));
	}

	public void add(Segment s) {
		addLast(s);
	}
	
	public Segment get(int index) throws IndexedAccessException {
		if(!indexed) throw new IndexedAccessException("You tried to access an index of a SrPath without indexing it first. Try calling indexPath after you finish building you path. This needs to be called when the path is modified and only before accessing an index.");
		return segArray.get(index);
	}
	
	public Segment getFirst() {
		return segList.getFirst();
	}
	
	public Segment getLast() {
		return segList.getLast();
	}
	
	public Path path(Graph g) {
		Path path = new Path();
		for(int i = 0; i < size(); i++) {
			Segment cur = get(i);
			if(i - 1 >= 0) {
				Segment prev = get(i - 1);
				Path sp = Graphs.dijkstraSP(g, prev.s2(), cur.s1(), "igp");
				if(sp != null) {
					path.concat(sp);
				}
			}
			if(cur.isAdj()) {
				path.add(cur.getEdge());
			}
		}
		return path;
	}
	
	public boolean isCycle() {
		return getFirst().s1() == getLast().s2();
	}
	
	public boolean contains(Edge e, Graph g) {
		for(Segment s : segList) {
			if(s.isAdj() && s.getEdge().getIndex() == e.getIndex()) {
				return true;
			}
		}
		for(int i = 1; i < size(); i++) {
			Path sp = Graphs.dijkstraSP(g, get(i - 1).s2(), get(i).s1(), "igp");
			if(sp != null && sp.contains(e)) return true;
		}
		return false;
	}
	
	public void addFirst(Segment s) {
		indexed = false;
		if(s.isNode()) nbNodeSeg += 1;
		if(s.isAdj()) nbAdjSeg += 1;
		segList.addFirst(s);
	}
	
	public void addFirst(int v) {
		addFirst(new Segment(v));
	}
	
	public void addFirst(Edge e) {
		addFirst(new Segment(e));
	}
	
	
	public void addLast(Segment s) {
		if(s.isNode()) nbNodeSeg += 1;
		if(s.isAdj()) nbAdjSeg += 1;
		segList.addLast(s);
		segArray.add(s);
	}
	
	public void removeLast() {
		indexed = false;
		Segment last = segList.removeLast();
		if(last.isAdj()) nbAdjSeg -= 1;
		else nbNodeSeg -= 1;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		int i = 0;
		for(Segment s : segList) {
			sb.append(s.toString());
			if(i < segList.size() - 1) sb.append(", ");
			i++;
		}
		sb.append(">");
		return sb.toString();
	}
	
	public String toIndexString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		int i = 0;
		for(Segment s : segList) {
			sb.append(s.toIndexString());
			if(i < segList.size() - 1) sb.append(", ");
			i++;
		}
		sb.append(">");
		return sb.toString();
	}
	
	public String toFileString(Graph g) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(Segment s : segList) {
			if(s.isNode()) {
				sb.append(g.getNodeLabel(s.s1()));
			} else {
				sb.append(s.getEdge().getIndex());
			}
			if(i < segList.size() - 1) {
				sb.append(' ');
			}
			i++;
		}
		return sb.toString();
	}

	@Override
	public Iterator<Segment> iterator() {
		return segList.iterator();
	}

}
