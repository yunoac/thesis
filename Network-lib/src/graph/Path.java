package graph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

import dataStructures.Edge;

public class Path implements Iterable<Edge> {

	private BitSet edgeSet;
	private ArrayList<Integer> nodes;
	private ArrayList<Edge> edges;
	private Graph g;
	private double cost;
	
	public Path() {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		edgeSet = new BitSet();
	}
	
	public Path(int orig, int dest, Edge[] parent) {
		Edge cur = parent[dest];
		LinkedList<Edge> tmp = new LinkedList<>();
		while(cur != null) {
			tmp.addFirst(cur);
			cur = parent[cur.orig()];
		}
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		edgeSet = new BitSet();
		for(Edge e : tmp) add(e);
	}
	
	public Path(LinkedList<Edge> edgeList) {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		edgeSet = new BitSet();
		for(Edge e : edgeList) add(e);
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void setGraph(Graph g) {
		this.g = g;
	}
	
	public void add(Edge edge) {
		if(edges.size() == 0) {
			edges.add(edge);
			nodes.add(edge.orig());
			nodes.add(edge.dest());
		} else {
			int last = lastNode();
			if(last != edge.orig()) throw new IllegalArgumentException(String.format("The path ends on node %d but you added an edge starting on node %d", last, edge.orig()));
			edges.add(edge);
			nodes.add(edge.dest());
		}
		edgeSet.set(edge.getIndex());
	}
	
	public void concat(Path p) {
		for(Edge e : p) {
			add(e);
		}
	}
	
	public boolean contains(Edge e) {
		return edgeSet.get(e.getIndex());
	}
	
	public int V() {
		return nodes.size();
	}
	
	public int E() {
		return edges.size();
	}
	
	public Edge getEdge(int index) {
		return edges.get(index);
	}
	
	public int getNode(int index) {
		return nodes.get(index);
	}
	
	public int firstNode() {
		return nodes.get(0);
	}
	
	public int lastNode() {
		return nodes.get(nodes.size() - 1);
	}
	
	public boolean isCycle() {
		return firstNode() == lastNode();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < nodes.size(); i++) {
			if(g != null) sb.append(g.getNodeLabel(nodes.get(i)));
			else sb.append(nodes.get(i));
			if(i < nodes.size() - 1) sb.append(" ");
		}
		return sb.toString();
	}
	
	public String toIndexString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < nodes.size(); i++) {
			sb.append(nodes.get(i));
			if(i < nodes.size() - 1) sb.append(" ");
		}
		return sb.toString();
	}
	

	@Override
	public Iterator<Edge> iterator() {
		return edges.iterator();
	}
	
}

