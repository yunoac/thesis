package sr;

import java.util.BitSet;

import dataStructures.Edge;
import graph.Graph;
import graph.Graphs;
import graph.TopologicalSort;

public class FailGraphs {
	
	private BitSet[][] fail;
	
	public FailGraphs(Graph g, ForwGraphs forw) {
		// initialize the empty failure graphs
		fail = new BitSet[g.V()][g.V()];
		for(int i = 0; i < g.V(); i++) {
			for(int j = 0; j < g.V(); j++) {
				fail[i][j] = new BitSet();
				fail[i][j].or(forw.getForwE(i, j));
			}
		}
		// compute the failure graphs based on the topological orders
		Edge[] edges = g.getEdgesByIndex();
		double[] igp = g.getWeigthFunction("igp").toArray();
		for(int x = 0; x < g.V(); x++) {
			for(Edge e : edges) {
				// check whether we can ignore edge e to accelerate the process
				if(!forw.belongsToIGPShortestPath(x, e)) continue;
				if(forw.getDag(x).inDeg(e.dest()) > 1) continue;
				// define forwxe such that forwxe[y] = forw(G \ e, x, y)
				BitSet[] forwxe = new BitSet[g.V()];
				for(int y = 0; y < g.V(); y++) {
					forwxe[y] = new BitSet();
				}
				// deactivate the edge
				e.setActive(false);
				Graph spxe = Graphs.dijkstraDag(g, igp, x);
				int[] order = new TopologicalSort(spxe).order();
				for(int i = 1; i < g.V(); i++) {
					int xi = order[i];
					for(Edge f : spxe.inEdges(xi)) {
						forwxe[xi].or(forwxe[f.orig()]);
						forwxe[xi].set(f.getIndex());
					}
				}
				for(int y = 0; y < g.V(); y++) {
					fail[x][y].or(forwxe[y]);
				}
				// reactivate the edge
				e.setActive(true);
			}
		}
	}
	
	public BitSet getFailE(int u, int v) {
		return fail[u][v];
	}

}
