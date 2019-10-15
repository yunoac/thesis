package sr;

import java.util.ArrayList;
import java.util.BitSet;

import dataStructures.Bitset;
import dataStructures.Edge;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import dataStructures.Triple;
import graph.Dags;
import graph.Graph;
import graph.Graphs;
import graph.TopologicalSort;
import graph.WeightFunction;
import utils.ArrayExt;
import utils.Cmp;
import utils.MyAssert;

public class ForwGraphs {

	private Graph g;
	private Graph[] dag;
	private Edge[] edges;
	private double[][] forwLat;
	private double[][] igpAPSP;
	private double[][] forwCap;
	private Bitset[][] forwE, forwN;
	private int[][] topoOrder;
	
	public ForwGraphs(Graph g) {
		this.g = g;
		edges = g.getEdgesByIndex();
		WeightFunction igp = g.getWeigthFunction("igp");
		igpAPSP = Graphs.floydWarshal(g, igp);
		dag = new Graph[g.V()];
		topoOrder = new int[g.V()][];
		for(int orig = 0; orig < g.V(); orig++) {
			dag[orig] = new Graph(g.nodeLabels());
			for(Edge e : edges) {
				if(belongsToIGPShortestPath(orig, e)) {
					dag[orig].addEdge(e);
				}
			}
			topoOrder[orig] = new TopologicalSort(dag[orig]).order();
		}
		forwLat = new double[g.V()][g.V()];
		WeightFunction lat = g.getWeigthFunction("lat");
		for(int orig = 0; orig < g.V(); orig++) {
			forwLat[orig] = Dags.longestPaths(dag[orig], orig, lat);
		}
		forwCap = new double[g.V()][g.V()];
		for(int orig = 0; orig < g.V(); orig++) {
			for(int dest = 0; dest < g.V(); dest++) {
				forwCap[orig][dest] = getLoads(orig, dest).third();
			}
		}
		forwE = new Bitset[g.V()][g.V()];
		for(int orig = 0; orig < g.V(); orig++) {
			for(int dest = 0; dest < g.V(); dest++) {
				forwE[orig][dest] = new Bitset();
			}
		}
		for(int orig = 0; orig < g.V(); orig++) {
			for(int dest : topoOrder[orig]) {
				for(Edge e : dag[orig].inEdges(dest)) {
					forwE[orig][dest].or(forwE[orig][e.orig()]);
					forwE[orig][dest].set(e.getIndex());
				}
			}
		}
		forwN = new Bitset[g.V()][g.V()];
		for(int orig = 0; orig < g.V(); orig++) {
			for(int dest = 0; dest < g.V(); dest++) {
				forwN[orig][dest] = new Bitset();
			}
		}
		for(int orig = 0; orig < g.V(); orig++) {
			for(int dest = 0; dest < g.V(); dest++) {
				for(int e : forwE[orig][dest]) {
					Edge edge = edges[e];
					forwN[orig][dest].set(edge.orig());
					forwN[orig][dest].set(edge.dest());
				}
			}
		}
	}
	
	public Bitset getForwE(int orig, int dest) {
		return forwE[orig][dest];
	}
	
	public Bitset getForwN(int orig, int dest) {
		return forwN[orig][dest];
	}
	
	public Bitset getSrPathE(SrPath p) {
		p.indexPath();
		Bitset E = new Bitset();
		for(int i = 0; i < p.size(); i++) {
			if(p.get(i).isAdj()) E.set(p.get(i).getEdge().getIndex());
		}
		for(int i = 1; i < p.size(); i++) {
			E.or(getForwE(p.get(i - 1).s2(), p.get(i).s1()));
		}
		return E;
	}
	
	public double[][] getForwCap() {
		return forwCap;
	}
	
	public double getForwCap(int orig, int dest) {
		return forwCap[orig][dest];
	}

	public double[][] getForwLat() {
		return forwLat;
	}

	public double getForwLat(int orig, int dest) {
		return forwLat[orig][dest];
	}
	
	public Graph getDag(int s) {
		return dag[s];
	}

	public Triple<double[], double[], Double> getLoads(int orig, int dest) {
		Graph dag = getForwGraphSubgraph(orig, dest);
		NodeQueue Q = new NodeQueue();
		Q.add(orig);
		double[] nodeRatio = new double[g.V()];
		double[] edgeRatio = new double[g.E()];
		nodeRatio[orig] = 1;
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : dag.outEdges(cur)) {
				double flow = nodeRatio[cur] / dag.outDeg(cur);
				edgeRatio[e.getIndex()] += flow;
				nodeRatio[e.dest()] += flow;
				if(!Q.visited(e.dest())) {
					Q.add(e.dest());
				}
			}
		}
		if(orig == dest) return new Triple<>(nodeRatio, edgeRatio, Double.POSITIVE_INFINITY);
		if(!Q.visited(dest)) return new Triple<>(nodeRatio, edgeRatio, 0.0);
		double min = Double.POSITIVE_INFINITY;
		for(Edge e : dag.getEdgesByIndex()) {
			MyAssert.assertTrue(Cmp.gr(edgeRatio[e.getIndex()], 0));
			min = Math.min(min, g.getWeight("bdw", e) / edgeRatio[e.getIndex()]);
		}
		return new Triple<>(nodeRatio, edgeRatio, min);
	}

	public Graph getForwGraphSubgraph(int orig, int dest) {
		Graph fw = new Graph(g.nodeLabels());
		for(int v = 0; v < dag[orig].V(); v++) {
			for(Edge e : dag[orig].outEdges(v)) {
				if(belongsToIGPShortestPath(orig, dest, e)) {
					fw.addEdge(e);
				}
			}
		}
		return fw;
	}

	public int spDagInDeg(int orig, int dest, int v) {
		int outdeg = 0;
		for(Edge e : g.inEdges(v)) {
			if(belongsToIGPShortestPath(orig, dest, e)) {
				outdeg += 1;
			}
		}
		return outdeg;
	}

	public ArrayList<Edge> spDagInEdges(int orig, int dest, int v) {
		ArrayList<Edge> inE = new ArrayList<>();
		for(Edge e : g.inEdges(v)) {
			if(belongsToIGPShortestPath(orig, dest, e)) {
				inE.add(e);
			}
		}
		return inE;
	}

	public int spDagOutDeg(int orig, int dest, int v) {
		int outdeg = 0;
		for(Edge e : g.outEdges(v)) {
			if(belongsToIGPShortestPath(orig, dest, e)) {
				outdeg += 1;
			}
		}
		return outdeg;
	}

	public ArrayList<Edge> spDagOutEdges(int orig, int dest, int v) {
		ArrayList<Edge> outE = new ArrayList<>();
		for(Edge e : g.outEdges(v)) {
			if(belongsToIGPShortestPath(orig, dest, e)) {
				outE.add(e);
			}
		}
		return outE;
	}

	public Graph getForwGraphSubgraph(SrPath srp) {
		Graph fw = new Graph(g.nodeLabels());
		for(int i = 0; i < srp.size(); i++) {
			if(srp.get(i).isAdj()) {
				fw.addEdge(srp.get(i).getEdge());
			}
		}
		for(int i = 1; i < srp.size(); i++) {
			Graph tmp = getForwGraphSubgraph(srp.get(i - 1).s2(), srp.get(i).s1());
			System.out.println(tmp);
			System.out.println();
			fw.join(tmp);
		}
		return fw;
	}

	public boolean belongsToIGPShortestPath(int orig, int dest, Edge e) {
		if(igpAPSP[orig][e.orig()] == Double.POSITIVE_INFINITY) return false;
		if(igpAPSP[e.dest()][dest] == Double.POSITIVE_INFINITY) return false;
		if(igpAPSP[orig][dest] == Double.POSITIVE_INFINITY) return false;
		return Cmp.eq(igpAPSP[orig][e.orig()] + g.getWeight("igp", e) + igpAPSP[e.dest()][dest], igpAPSP[orig][dest]);
	}

	public boolean belongsToIGPShortestPath(int orig, Edge e) {
		if(igpAPSP[orig][e.orig()] == Double.POSITIVE_INFINITY) return false;
		return Cmp.eq(igpAPSP[orig][e.orig()] + g.getWeight("igp", e), igpAPSP[orig][e.dest()]);
	}

}
