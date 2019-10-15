package graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import dataStructures.Bitset;
import dataStructures.Edge;
import dataStructures.Index;
import dataStructures.LatLon;
import dataStructures.OrderablePair;
import dataStructures.Pair;

public class Graph {

	private int V, E;
	private LinkedList<Edge>[] outE, inE;
	private HashMap<String, WeightFunction> wfs;
	private Index<String> nodeLabels;
	private LatLon[] positions;
	private String name;
	private Bitset edgeSet;

	public Graph(int V) {
		this.V = V;
		init();
	}

	public Graph(Index<String> nodeLabels) {
		this.nodeLabels = nodeLabels;
		this.V = nodeLabels.size();
		init();
	}
	
	public Graph(Index<String> nodeLabels, LatLon[] positions) {
		this.nodeLabels = nodeLabels;
		this.V = nodeLabels.size();
		this.positions = positions;
		init();
	}
	
	public LatLon getPosition(int v) {
		return positions[v];
	}
	
	public HashMap<String, WeightFunction> getWeightFunctions() {
		return wfs;
	}
	
	public void setWeightFunctions(HashMap<String, WeightFunction> wfs) {
		this.wfs = wfs;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		outE = new LinkedList[V];
		inE = new LinkedList[V];
		for(int v = 0; v < V; v++) {
			outE[v] = new LinkedList<>();
			inE[v] = new LinkedList<>();
		}
		wfs = new HashMap<>();
		if(nodeLabels == null) {
			nodeLabels = new Index<>();
			for(int v = 0; v < V; v++) {
				nodeLabels.add(v + "");
			}
		}
		edgeSet = new Bitset();
	}
	
	public void join(Graph other) {
		if(V != other.V) throw new IllegalArgumentException("can only join if the two graphs have the same number of vertices");
		for(int v = 0; v < other.V; v++) {
			for(Edge e : other.outEdges(v)) {
				addEdge(e);
			}
		}
	}
	
	public Index<String> nodeLabels() {
		return nodeLabels;
	}
	
	public String getNodeLabel(int v) {
		return nodeLabels.get(v);
	}
	
	public int getNodeIndex(String v) {
		return nodeLabels.get(v);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Graph deepCopy() {
		Graph cp = new Graph(V);
		for(int x = 0; x < V; x++) {
			for(Edge e : outE[x]) {
				cp.addEdge(e.deepCopy());
			}
		}
		return cp;
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}
	
	public void connect(int u, int v) {
		connect(u, v, E);
	}
	
	public void connectBoth(int u, int v) {
		connect(u, v, E);
		connect(v, u, E);
	}
	
	public void connect(int u, int v, int index) {
		Edge e = new Edge(u, v, index);
		addEdge(e);
	}
		
	public void connect(String u, String v) {
		connect(u, v, E);
	}
	
	public void connectBoth(String u, String v) {
		connect(u, v, E);
		connect(v, u, E);
	}
	
	public Edge[] getEdgesByIndex() {
		Edge[] edges = new Edge[E];
		int i = 0;
		for(int v = 0; v < V; v++) {
			for(Edge e : outEdges(v)) {
				edges[i++] = e;
			}
		}
		Arrays.sort(edges);
		return edges;
	}
	
	public void connect(String u, String v, int index) {
		Edge e = new Edge(nodeLabels.get(u), nodeLabels.get(v), index);
		addEdge(e);
	}
	
	public void addEdge(Edge e) {
		outE[e.orig()].add(e);
		inE[e.dest()].add(e);
		e.setGraph(this);
		edgeSet.set(e.getIndex());
		E += 1;
	}
	
	public Bitset getEdgeSet() {
		return edgeSet;
	}

	public LinkedList<Edge> outEdges(int x) {
		return outE[x];
	}
	
	public int outDeg(int x) {
		return outE[x].size();
	}

	public LinkedList<Edge> inEdges(int x) {
		return inE[x];
	}
	
	public int inDeg(int x) {
		return inE[x].size();
	}

	public void createWeightFuntion(String label) {
		wfs.put(label, new WeightFunction());
	}

	public void addWeightFunction(String label, WeightFunction wf) {
		wfs.put(label, wf);
	}

	public WeightFunction getWeigthFunction(String label) {
		return wfs.get(label);
	}

	public double getWeight(String label, Edge e) {
		return wfs.get(label).getWeight(e);
	}
	
	public void setWeight(String label, Edge e, double w) {
		wfs.get(label).setWeight(e, w);
	}
	
	public Set<String> getWeightLabels() {
		return wfs.keySet();
	}
	
	public boolean containsWeightFunction(String label) {
		return wfs.containsKey(label);
	}
	
	public void setWeightFunction(String label, WeightFunction w) {
		wfs.put(label, w);
	}
	
	public Graph transpose() {
		Graph gt = new Graph(nodeLabels);
		for(int v = 0; v < V; v++) {
			for(Edge e : outEdges(v)) {
				Edge et = new Edge(e.dest(), e.orig(), e.getIndex());
				gt.addEdge(et);
			}
		}
		return gt;
	}
	
	public String toLabelString() {
		StringBuilder sb = new StringBuilder();
		for(int v = 0; v < V; v++) {
			for(Edge e : outEdges(v)) {
				sb.append(e.toLabelString() + "\n");
			}
		}
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int v = 0; v < V; v++) {
			for(Edge e : outEdges(v)) {
				sb.append(e.toIndexString() + "\n");
			}
		}
		return sb.toString();
	}
	
	public String sortedEdgeListString() {
		LinkedList<OrderablePair<String, String>> edges = new LinkedList<>();
		for(int v = 0; v < V; v++) {
			for(Edge e : outEdges(v)) {
				edges.add(new OrderablePair<>(getNodeLabel(e.orig()), getNodeLabel(e.dest())));
			}
		}
		Collections.sort(edges);
		StringBuilder sb = new StringBuilder();
		for(OrderablePair<String, String> e : edges) {
			sb.append(e.first() + " " + e.second() + "\n");			
		}
		return sb.toString();
	}

}
