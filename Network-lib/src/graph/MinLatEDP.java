package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import graph.FlowGraph.FlowEdge;
import utils.MyAssert;

public class MinLatEDP {

	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, Collection<Integer> S, Collection<Integer> T, int maxPath) {
		FlowGraph gf = new FlowGraph(g, S, T);
		// compute flow
		int maxflow = 0;
		double mincost = 0;
		while(maxflow < maxPath) {
			// perform bellman-ford
			FlowEdge[] parent = new FlowEdge[gf.V()];
			double[] dist = new double[gf.V()];
			Arrays.fill(dist, Integer.MAX_VALUE);
			dist[gf.getS()] = 0;
			// perform Bellman-Ford iteration
			for(int k = 0; k < gf.V(); k++) {
				// relax every edge of the graph
				for(int x = 0; x < gf.V(); x++) {
					for(FlowEdge e : gf.outEdges(x)) {
						if(e.getCap() > 0 && dist[e.orig()] + e.getCost() < dist[e.dest()] - 1e-6) {
							dist[e.dest()] = dist[e.orig()] + e.getCost();
							parent[e.dest()] = e;
						}
					}
				}
			}
			if(parent[gf.getT()] == null) break;
			// build the path and push flow
			FlowEdge cur = parent[gf.getT()];
			while(cur != null) {
				mincost += cur.push(1);
				cur = parent[cur.orig()];
			}
			maxflow += 1;
		}
		// compute paths from flow
		ArrayList<Path> paths = new ArrayList<>();
		while(true) {
			// perform BFS
			NodeQueue Q = new NodeQueue();
			FlowEdge[] parent = new FlowEdge[gf.V()];
			Q.add(gf.getS());
			while(!Q.isEmpty()) {
				int cur = Q.poll();
				for(FlowEdge e : gf.outEdges(cur)) {
					if(e.getFlow() > 0 && !Q.visited(e.dest())) {
						Q.add(e.dest());
						parent[e.dest()] = e;
					}
				}
			}
			if(parent[gf.getT()] == null) break;
			FlowEdge cur = parent[gf.getT()];
			LinkedList<Edge> pathEdges = new LinkedList<>();
			while(cur != null) {
				cur.addFlow(-1);
				Edge e = cur.getOriginalEdge();
				pathEdges.addFirst(e);
				cur = parent[cur.orig()];
			}
			pathEdges.removeFirst();
			pathEdges.removeLast();			
			paths.add(new Path(pathEdges));
		}
		MyAssert.assertTrue(paths.size() == maxflow, paths.size() + " " + maxflow);
		return new Pair<>(paths, maxflow == 0 ? Double.POSITIVE_INFINITY : mincost);	
	}
	
	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, Collection<Integer> S, Collection<Integer> T) {
		return computePaths(g, S, T, Integer.MAX_VALUE);
	}
	
	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, int s, int t) {
		return computePaths(g, s, t, Integer.MAX_VALUE);
	}
	
	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, int[] s, int[] t) {
		return computePaths(g, s, t, Integer.MAX_VALUE);
	}
	
	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, int s, int t, int maxPath) {
		LinkedList<Integer> S = new LinkedList<>();
		S.add(s);
		LinkedList<Integer> T = new LinkedList<>();
		T.add(t);
		return computePaths(g, S, T, maxPath);
	}
	
	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, int s1, int s2, int t1, int t2, int maxPath) {
		LinkedList<Integer> S = new LinkedList<>();
		S.add(s1);
		S.add(s2);
		LinkedList<Integer> T = new LinkedList<>();
		T.add(t1);
		T.add(t2);
		return computePaths(g, S, T, maxPath);
	}
	
	public static Pair<ArrayList<Path>, Double> computePaths(Graph g, int[] s, int[] t, int maxPath) {
		LinkedList<Integer> S = new LinkedList<>();
		for(int src : s) S.add(src);
		LinkedList<Integer> T = new LinkedList<>();
		for(int dst : t) T.add(dst);
		return computePaths(g, S, T, maxPath);
	}
	
}
