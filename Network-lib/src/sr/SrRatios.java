package sr;

import dataStructures.Edge;
import graph.Graph;
import graph.Graphs;
import graph.TopologicalSort;

public class SrRatios {
	
	private Graph g;
	private ForwGraphs forw;
	private double[][][] nodeLoad;
	private double[][][] edgeLoad;
	
	public SrRatios(Graph g, ForwGraphs forw) {
		this.g = g;
		this.forw = forw;
	}
	
	public SrRatios(Graph g) {
		this.g = g;
		this.forw = new ForwGraphs(g);
	}
	
	
	private void computeLoadsDefinition(Graph g, ForwGraphs forw) {
		nodeLoad = new double[g.V()][g.V()][g.V()];
		double[] igp = g.getWeigthFunction("igp").toArray();
		for(int s = 0; s < g.V(); s++) {
			Graph dag = Graphs.dijkstraDag(g, igp, s);
			TopologicalSort tp = new TopologicalSort(dag);
			for(int t = 0; t < g.V(); t++) {
				for(int v : tp.order()) {
					if(v == s) {
						nodeLoad[s][t][v] = 1;
					} else {
						for(Edge e : forw.spDagInEdges(s, t, v)) {
							int u = e.orig();
							nodeLoad[s][t][v] += nodeLoad[s][t][u] / forw.spDagOutDeg(s, t, u);
						}
					}
				}
			}
		}
		edgeLoad = new double[g.V()][g.V()][g.E()];
		Edge[] edges = g.getEdgesByIndex();
		for(int s = 0; s < g.V(); s++) {
			for(int t = 0; t < g.V(); t++) {
				for(Edge e : edges) {
					edgeLoad[s][t][e.getIndex()] = nodeLoad[s][t][e.orig()] / forw.spDagOutDeg(s, t, e.orig());
				}
			}
		}	
	}

}
