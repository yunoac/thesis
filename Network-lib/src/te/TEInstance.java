package te;

import java.util.ArrayList;

import dataStructures.Edge;
import graph.Graph;

public class TEInstance {

	private Graph g;
	private ArrayList<Demand> demands;
	private Edge[] edges;
	
	public TEInstance(Graph g, ArrayList<Demand> demands) {
		this.g = g;
		this.demands = demands;
		this.edges = g.getEdgesByIndex();
	}

	public Graph getGraph() {
		return g;
	}
	
	public ArrayList<Demand> getDemands() {
		return demands;
	}
	
	public Demand getDemand(int i) {
		return demands.get(i);
	}
	
	public Edge getEdge(int i) {
		return edges[i];
	}
	
	public Edge[] getEdges() {
		return edges;
	}
	
	public int E() {
		return edges.length;
	}
	
	public int D() {
		return demands.size();
	}
	
}
