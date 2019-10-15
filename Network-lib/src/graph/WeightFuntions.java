package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import dataStructures.Edge;
import utils.Cmp;

public class WeightFuntions {

	/*
	 * Check whether a given weight function is complete. 
	 * 
	 * A weight function is complete iff every edge
	 * belongs to a shortest path. In other words, every edge is
	 * a shortest path between its end points.
	 */
	public static boolean isComplete(Graph g, WeightFunction w) {
		return nonShortestPathEdges(g, w).isEmpty();
	}
	
	public static boolean isComplete(Graph g, String weightLbl) {
		return nonShortestPathEdges(g, g.getWeigthFunction(weightLbl)).isEmpty();
	}
	
	public static ArrayList<Edge> nonShortestPathEdges(Graph g, String weightLbl) {
		return nonShortestPathEdges(g, g.getWeigthFunction(weightLbl));
	}

	public static ArrayList<Edge> nonShortestPathEdges(Graph g, WeightFunction w) {
		// compute all pairs shortest paths
		double[][] distance = Graphs.floydWarshal(g, w);
		// loop over all edges
		ArrayList<Edge> nonSP = new ArrayList<>();
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				// check whether e is the shortest path between its end points
				if(!Cmp.eq(w.getWeight(e), distance[e.orig()][e.dest()])) {
					nonSP.add(e);
				}
			}
		}
		return nonSP;
	}

	/*
	 * Check whether a given weight function is ECMP-free.
	 * 
	 * A weight function is ECMP-free iff there is a unique shortest
	 * path between any pair of connected nodes in the graph.
	 */
	public static boolean isECMPFree(Graph g, WeightFunction w) {
		for(int orig = 0; orig < g.V(); orig++) {
			// compute all parent in shortest paths starting at the origin
			LinkedList<Edge>[] parents = Graphs.dijsktraAllParents(g, w, orig).second();
			// initialize a queue to perform a reverse BFS
			Queue<Integer> Q = new LinkedList<>();
			// check whether there is a destination with ECMP
			for(int dest = 0; dest < g.V(); dest++) {
				if(parents[dest] == null) continue;
				Q.add(dest);
				Integer[] inDeg = new Integer[g.V()];
				// set the in degree as the number of parents in the sp-dag
				inDeg[dest] = parents[dest].size();
				while(!Q.isEmpty()) {
					int cur = Q.poll();
					if(inDeg[cur] > 1) return false;
					for(Edge e : parents[cur]) {
						// check whether the origin has already been processed
						if(inDeg[e.orig()] == null) {
							// set the in degree as the number of parents in the sp-dag
							inDeg[e.orig()] = parents[e.orig()].size();
							Q.add(e.orig());
						}
					}
				}
			}
		}
		return true;
	}

}
