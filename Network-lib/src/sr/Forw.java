package sr;

import dataStructures.Edge;
import graph.Graph;
import graph.Graphs;
import utils.Cmp;
import utils.MyAssert;

public class Forw {

	private Graph g;
	private double[][] igp;
	private Graph[][] dag;
	
	public Forw(Graph g) {
		this.g = g;
		igp = Graphs.floydWarshal(g, "igp");
		/*
		dag = new Graph[g.V()][g.V()];
		for(int v = 0; v < g.V(); v++) {
			for(int u = 0; u < g.V(); u++) {
				dag[v][u] = Graphs.shortestPathDag(g, v, u, igp, "igp");
			}
		}
		*/
	}
	
	public Graph dag(int orig, int dest) {
		return dag[orig][dest];
	}
	
	public double igp(int orig, int dest) {
		return igp[orig][dest];
	}
	
	/*
	public boolean dagContainsEdge(int orig, int dest, Edge e) {
		return dag[orig][dest].getEdgeSet().get(e.index());
	}
	*/
	
	public boolean dagContainsEdge(int orig, int dest, Edge e) {
		return Cmp.eq(igp[orig][e.orig()] + g.getWeight("igp", e) + igp[e.dest()][dest], igp[orig][dest]);
	}
	
}
