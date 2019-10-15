package graph;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import utils.Cmp;

public class Dags {
	
	public static BigInteger pathCount(Graph g, int orig, int dest) {
		BigInteger[] pc = new BigInteger[g.V()];
		Arrays.fill(pc, BigInteger.ZERO);
		DFS dfs = new DFS(g);
		pc[orig] = BigInteger.ONE;
		for(int v : dfs.topoOrder()) {
			for(Edge e : g.inEdges(v)) {
				pc[v] = pc[v].add(pc[e.orig()]);
			}
		}
		return pc[dest];
	}
	
	public static double[] shortestPaths(Graph g, int source, WeightFunction w) {
		double[] sp = new double[g.V()];
		Arrays.fill(sp, Double.POSITIVE_INFINITY);
		DFS dfs = new DFS(g);
		sp[source] = 0;
		for(int v : dfs.topoOrder()) {
			for(Edge e : g.inEdges(v)) {
				sp[v] = Math.min(sp[v], sp[e.orig()] + w.getWeight(e));
			}
		}
		return sp;
	}
	

	public static double shortestPaths(Graph g, String orig, String dest, String weightLbl) {
		return shortestPath(g, g.getNodeIndex(orig), g.getNodeIndex(dest), weightLbl);	
	}
	
	public static double shortestPath(Graph g, int orig, int dest, String weightLbl) {
		return shortestPaths(g, orig, g.getWeigthFunction(weightLbl))[dest];	
	}
	
	public static double shortestPath(Graph g, int orig, int dest, WeightFunction w) {
		return shortestPaths(g, orig, w)[dest];	
	}
	
	public static double[] longestPaths(Graph g, int source, WeightFunction w) {
		double[] lp = new double[g.V()];
		Arrays.fill(lp, Double.NEGATIVE_INFINITY);
		DFS dfs = new DFS(g);
		lp[source] = 0;
		for(int v : dfs.topoOrder()) {
			for(Edge e : g.inEdges(v)) {
				lp[v] = Math.max(lp[v], lp[e.orig()] + w.getWeight(e));
			}
		}
		return lp;
	}
	
	public static double longestPath(Graph g, String orig, String dest, String weightLbl) {
		return longestPath(g, g.getNodeIndex(orig), g.getNodeIndex(dest), weightLbl);	
	}
	
	public static double longestPath(Graph g, int orig, int dest, String weightLbl) {
		return longestPaths(g, orig, g.getWeigthFunction(weightLbl))[dest];	
	}
	
	public static double longestPath(Graph g, int orig, int dest, WeightFunction w) {
		return longestPaths(g, orig, w)[dest];	
	}

	public static double[] dagAveragePathCost(Graph g, int source, WeightFunction w) {
		DFS dfs = new DFS(g);
		long[] count = new long[g.V()];
		count[source] = 1;
		double[] sum = new double[g.V()];
		for(int v : dfs.topoOrder()) {
			for(Edge e : g.inEdges(v)) {
				count[v] += count[e.orig()];
				sum[v] += sum[e.orig()] + count[e.orig()] * w.getWeight(e);
			}
		}
		double[] averages = new double[g.V()];
		for(int v = 0; v < g.V(); v++) {
			averages[v] = sum[v] / count[v];
		}
		return averages;
	}
	
	public static double[] dagAveragePathCostBig(Graph g, int source, WeightFunction w) {
		DFS dfs = new DFS(g);
		BigDecimal[] count = new BigDecimal[g.V()];
		Arrays.fill(count, BigDecimal.ZERO);
		count[source] = BigDecimal.ONE;
		BigDecimal[] sum = new BigDecimal[g.V()];
		Arrays.fill(sum, BigDecimal.ZERO);
		for(int v : dfs.topoOrder()) {
			for(Edge e : g.inEdges(v)) {
				count[v] = count[v].add(count[e.orig()]);
				sum[v] = sum[v].add( sum[e.orig()].add(count[e.orig()].multiply(BigDecimal.valueOf(w.getWeight(e)))));
			}
		}
		double[] averages = new double[g.V()];
		for(int v = 0; v < g.V(); v++) {
			if(count[v].compareTo(BigDecimal.ZERO) > 0) {
				averages[v] = sum[v].divide(count[v], MathContext.DECIMAL32).doubleValue();				
			}
		}
		return averages;
	}

	public static double dagAveragePathCostBig(Graph g, int source, int dest, WeightFunction w) {
		return dagAveragePathCostBig(g, source, w)[dest];
	}
	
	
	public static double dagAveragePathCostBig(Graph g, int source, int dest, String weightLbl) {
		return dagAveragePathCostBig(g, source, g.getWeigthFunction(weightLbl))[dest];
	}
	
	
	/*
	 * Given a dag, an origin node and the weight function to use, computes the list of the weights of
	 * all path between the origin and every other node.
	 * 
	 * The runtime is linear with respect to the output size but bare in mind that the number of paths
	 * is usually exponential so this will take a very long time to compute on a typical network.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Double>[] allPathCosts(Graph g, int orig, String weightLbl) {
		WeightFunction w = g.getWeigthFunction(weightLbl);
		ArrayList<Double>[] pathCosts = new ArrayList[g.V()];
		for(int v = 0; v < g.V(); v++) {
			pathCosts[v] = new ArrayList<>();
		}
		DFS dfs = new DFS(g);
		pathCosts[orig].add(0.0);
		for(int v : dfs.topoOrder()) {
			for(Edge e : g.inEdges(v)) {
				for(double c : pathCosts[e.orig()]) {
					pathCosts[v].add(c + w.getWeight(e));
				}
			}
			Collections.sort(pathCosts[v]);
		}
		return pathCosts;
	}
	
	/*
	 * Given a dag, an origin node, a destination node and the weight function to use, computes 
	 * the list of the weights of all path between the origin and the destination.
	 * 
	 * The runtime is linear with respect to the output size but bare in mind that the number of paths
	 * is usually exponential so this will take a very long time to compute on a typical network.
	 */
	public static ArrayList<Double> allPathCosts(Graph g, int orig, int dest, String weightLbl) {
		return allPathCosts(g, orig, weightLbl)[dest];
	}
	
	public static ArrayList<Pair<Path,Double>> pathDecomposition(Graph g, int orig, int dest) {
		return computeFlowPaths(g, orig, dest, new WeightFunction(g, 1.0));
	}

	public static ArrayList<Pair<Path,Double>> pathDecomposition(Graph g, int orig, int dest, String weightLbl) {
		return computeFlowPaths(g, orig, dest, g.getWeigthFunction(weightLbl));
	}

	public static ArrayList<Pair<Path, Double>> computeFlowPaths(Graph g, int orig, int dest, WeightFunction flow) {
		// initialize total and residual weight
		ArrayList<Pair<Path, Double>> paths = new ArrayList<>();
		double[] wres = flow.toArray();
		// build paths until the total weights is 0
		while(true) {
			// perform a BFS to find a path
			NodeQueue Q = new NodeQueue();
			Q.add(orig);
			double[] pcap = new double[g.V()];
			Edge[] parent = new Edge[g.V()];
			Arrays.fill(pcap, Double.POSITIVE_INFINITY);
			while(!Q.isEmpty()) {
				int cur = Q.poll();
				for(Edge e : g.outEdges(cur)) {
					if(!Q.visited(e.dest()) && Cmp.gr(wres[e.getIndex()], 0)) {
						pcap[e.dest()] = Math.min(pcap[e.orig()], wres[e.getIndex()]);
						parent[e.dest()] = e;
						Q.add(e.dest());
					}
				}
			}
			if(parent[dest] == null) break;
			// build the path and update residual weights
			Path path = new Path(orig, dest, parent);
			paths.add(new Pair<>(path, pcap[dest]));
			for(Edge e : path) {
				wres[e.getIndex()] -= pcap[dest];
			}
		}
		return paths;
	}
	
}
