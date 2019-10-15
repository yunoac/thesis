package graph;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import dataStructures.Bitset;
import dataStructures.Count;
import dataStructures.Edge;
import dataStructures.Index;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import utils.ArrayExt;
import utils.Cmp;
import utils.MyAssert;

public class Graphs {
	
	public static Graph transpose(Graph g) {
		Graph gt = new Graph(g.V());
		for(Edge e : g.getEdgesByIndex()) {
			Edge et = new Edge(e.dest(), e.orig(), e.getIndex());
			gt.addEdge(et);
		}
		return gt;
	}
	
	public static int maxOutDeg(Graph g) {
		int max = 0;
		for(int v = 0; v < g.V(); v++) {
			max = Math.max(max, g.outDeg(v));
		}
		return max;
	}
	
	public static int edgeDiameter(Graph g) {
		int diam = 0;
		for(int s = 0; s < g.V(); s++) {
			diam = Math.max(s, ArrayExt.max(bfsDist(g, s)));
		}
		return diam;
	}
	
	public static int[] bfsDist(Graph g, int s) {
		NodeQueue Q = new NodeQueue();
		Q.add(s);
		int[] dist = new int[g.V()];
		Arrays.fill(dist, Integer.MAX_VALUE);
		dist[s] = 0;
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : g.outEdges(cur)) {
				int next = e.dest();
				if(!Q.visited(next)) {
					dist[next] = 1 + dist[cur];
					Q.add(next);
				}
			}
		}
		return dist;
	}
	
	public static int[][] allBfsDist(Graph g) {
		int[][] dist = new int[g.V()][];
		for(int s = 0; s < g.V(); s++) {
			dist[s] = bfsDist(g, s);
		}
		return dist;
	}

	/*
	 * Given a graph checks whether for every edge (x, y)
	 * there is also an edge (y, x)
	 */
	public static boolean isUndirected(Graph g) {
		int[][] ecm = adjacencyMatrix(g);
		for(int i = 0; i < ecm.length; i++) {
			for(int j = i + 1; j < ecm.length; j++) {
				if(ecm[i][j] > 0 && ecm[j][i] == 0) return false;
			}
		}
		return true;
	}

	/*
	 * Given a graph, computes an VxV matrix A such that
	 * A[i][j] equals the number of edges from node i to node j
	 */
	public static int[][] adjacencyMatrix(Graph g) {
		int[][] A = new int[g.V()][g.V()];
		for(int x = 0; x < g.V(); x++) {
			for(Edge e : g.outEdges(x)) {
				A[e.orig()][e.dest()] += 1;
			}
		}
		return A;
	}

	/*
	 * Given an undirected graph, compute the number of connected
	 * components
	 * 
	 * @PRE: g is undirected
	 */
	public static int nbConenctedComponents(Graph g) {
		int nbcc = 0;
		Integer[] label = new Integer[g.V()];
		for(int s = 0; s < g.V(); s++) {
			if(label[s] == null) {
				nbcc += 1;
				bfsLabel(g, s, label);
			}
		}
		return nbcc;
	}

	/*
	 * Compute an array with the sizes of the connected components
	 * of g
	 * 
	 * @PRE: g is undirected
	 */
	public static int[] connectedComponentsSizes(Graph g) {
		Count<Integer> cnt = new Count<>(bfsLabel(g));
		return cnt.getCounts();
	}

	/*
	 * Performs a BFS and label the nodes
	 * 
	 */
	public static Integer[] bfsLabel(Graph g) {
		Integer[] label = new Integer[g.V()];
		for(int s = 0; s < g.V(); s++) {
			if(label[s] == null) {
				bfsLabel(g, s, label);
			}
		}	
		return label;
	}
	
	public static boolean connected(Graph g, int s, int t) {
		Integer[] label = new Integer[g.V()];
		bfsLabel(g, s, label);
		return label[t] != null;
	}

	/*
	 * Performs a BFS from node s and labels unlabeled nodes reachable
	 * from s with the label s
	 */
	public static void bfsLabel(Graph g, int s, Integer[] label) {
		if(label[s] != null) return;
		Queue<Integer> Q = new LinkedList<>();
		Q.add(s);
		label[s] = s;
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : g.outEdges(cur)) {
				if(label[e.dest()] == null) {
					label[e.dest()] = s;
					Q.add(e.dest());
				}
			}
		}
	}
	
	
	public static boolean connected(Graph g, int s, int t, BitSet forbidden) {
		Integer[] label = new Integer[g.V()];
		bfsLabel(g, s, label, forbidden);
		return label[t] != null;
	}

	public static void bfsLabel(Graph g, int s, Integer[] label, BitSet forbidden) {
		if(label[s] != null) return;
		Queue<Integer> Q = new LinkedList<>();
		Q.add(s);
		label[s] = s;
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : g.outEdges(cur)) {
				if(forbidden.get(e.getIndex())) continue;
				if(label[e.dest()] == null) {
					label[e.dest()] = s;
					Q.add(e.dest());
				}
			}
		}
	}

	public static Integer[] bfsLabelReverse(Graph g) {
		Integer[] label = new Integer[g.V()];
		for(int s = 0; s < g.V(); s++) {
			if(label[s] == null) {
				bfsLabelReverse(g, s, label);
			}
		}
		return label;
	}

	public static void bfsLabelReverse(Graph g, int s, Integer[] label) {
		if(label[s] != null) return;
		Queue<Integer> Q = new LinkedList<>();
		Q.add(s);
		label[s] = s;
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : g.inEdges(cur)) {
				if(label[e.dest()] == null) {
					label[e.dest()] = s;
					Q.add(e.dest());
				}
			}
		}
	}

	public static Integer[] bfsLabelReverse(Graph g, int s) {
		Integer[] label = new Integer[g.V()];
		Queue<Integer> Q = new LinkedList<>();
		Q.add(s);
		label[s] = s;
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : g.inEdges(cur)) {
				if(label[e.orig()] == null) {
					label[e.orig()] = s;
					Q.add(e.orig());
				}
			}
		}
		return label;
	}


	/*
	 * Compute the largest connected component of g
	 * 
	 * @PRE: g is undirected
	 */
	public static Graph largestCC(Graph g) {
		Integer[] labels = bfsLabel(g);
		Count<Integer> cnt = new Count<>(labels);
		Pair<Integer, Integer> lbl = cnt.mostCommon();
		boolean[] mask = ArrayExt.getEqualIndexes(labels, lbl.first());
		Graph cc = inducedSubgraph(g, mask);
		cc.setName(g.getName() + "[largestcc]");
		return cc;
	}
	
	public static Graph largestCC(Graph g, boolean keepName) {
		String name = g.getName();
		g = largestCC(g);
		g.setName(name);
		return g;
	}

	/*
	 * Compute the induced subgraph of g that contains the nodes v such
	 * that mask[v] is true
	 */
	public static Graph inducedSubgraph(Graph g, boolean[] mask) {
		Index<String> nodeLabels = new Index<>();
		for(int v = 0; v < g.V(); v++) {
			if(mask[v]) {
				nodeLabels.add(g.getNodeLabel(v));
			}
		}
		HashMap<String, WeightFunction> wfs = g.getWeightFunctions();
		HashMap<String, WeightFunction> wfsi = new HashMap<>();
		for(String key : wfs.keySet()) {
			wfsi.put(key, new WeightFunction());
		}
		Graph gi = new Graph(nodeLabels);
		int index = 0;
		for(int v = 0; v < g.V(); v++) {
			if(!mask[v]) continue;
			for(Edge e : g.outEdges(v)) {
				if(mask[e.dest()]) {
					int orig = nodeLabels.get(g.getNodeLabel(e.orig()));
					int dest = nodeLabels.get(g.getNodeLabel(e.dest()));
					Edge ei = new Edge(orig, dest, index++);
					for(String key : wfs.keySet()) {
						wfsi.get(key).setWeight(ei, g.getWeight(key, e));
					}
					gi.addEdge(ei);
				}
			}
		}
		gi.setWeightFunctions(wfsi);
		return gi;
	}

	/*
	 * Compute the induced subgraph of g that contains the nodes v such
	 * that mask[v] is true
	 */
	public static Graph inducedSubgraph(Graph g, Integer[] mask) {
		Index<String> nodeLabels = new Index<>();
		for(int v = 0; v < g.V(); v++) {
			if(mask[v] != null) {
				nodeLabels.add(g.getNodeLabel(v));
			}
		}
		Graph gi = new Graph(nodeLabels);
		int index = 0;
		for(int v = 0; v < g.V(); v++) {
			if(mask[v] == null) continue;
			for(Edge e : g.outEdges(v)) {
				if(mask[e.dest()] != null) {
					int orig = nodeLabels.get(g.getNodeLabel(e.orig()));
					int dest = nodeLabels.get(g.getNodeLabel(e.dest()));
					gi.addEdge(new Edge(orig, dest, index++));
				}
			}
		}
		return gi;
	}

	/*
	 * Computes an VxV matrix A such that A[u][v] is the
	 * shortest path distance between u and v with respect to w
	 */
	public static double[][] floydWarshal(Graph g, String weightLabel) {
		return 	floydWarshal(g, g.getWeigthFunction(weightLabel));
	}

	/*
	 * Computes an VxV matrix A such that A[u][v] is the
	 * shortest path distance between u and v with respect to w
	 */
	public static double[][] floydWarshal(Graph g, WeightFunction w) {
		return floydWarshal(g, w.toArray());
	}

	/*
	 * Computes an VxV matrix A such that A[u][v] is the
	 * shortest path distance between u and v with respect to w
	 */
	public static double[][] floydWarshal(Graph g, double[] w) {
		int n = g.V();
		double[][] min = new double[n][n];
		for(int x = 0; x < n; x++) {
			Arrays.fill(min[x], Double.POSITIVE_INFINITY);
			min[x][x] = 0;
		}
		// base case
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				if(w[e.getIndex()] < min[e.orig()][e.dest()]) {
					// we found a better, update min and set max
					min[e.orig()][e.dest()] = w[e.getIndex()];
				}
			}
		}
		// general case
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					double dikj = min[i][k] + min[k][j];
					if(dikj < min[i][j]) {
						// we found a better, update min and set max
						min[i][j] = dikj;
					}
				}
			}
		}
		return min;
	}


	/*
	 * Given a graph and two weight function wmin and wmax
	 * computes two matrices min and max such that:
	 * 
	 * min[i][j] is the cost of the shortest path from i to j wrt wmin
	 * max[i][j] is the maximum cost wrt wmax for a path among the shortest path from i to j
	 */
	public static Pair<double[][], double[][]> apspMinMax(Graph g, String wmin, String wmax) {
		return apspMinMax(g, g.getWeigthFunction(wmin), g.getWeigthFunction(wmax));
	}

	/*
	 * Given a graph and two weight function wmin and wmax
	 * computes two matrices min and max such that:
	 * 
	 * min[i][j] is the cost of the shortest path from i to j wrt wmin
	 * max[i][j] is the maximum cost wrt wmax for a path among the shortest path from i to j
	 */
	public static Pair<double[][], double[][]> apspMinMax(Graph g, WeightFunction wmin, WeightFunction wmax) {
		return apspMinMax(g, wmin.toArray(), wmax.toArray());
	}

	/*
	 * Given a graph and two weight function wmin and wmax
	 * computes two matrices min and max such that:
	 * 
	 * min[i][j] is the cost of the shortest path from i to j wrt wmin
	 * max[i][j] is the maximum cost wrt wmax for a path among the shortest path from i to j
	 */
	public static Pair<double[][], double[][]> apspMinMax(Graph g, double[] wmin, double[] wmax) {
		int n = g.V();
		double[][] min = new double[n][n];
		double[][] max = new double[n][n];
		for(int x = 0; x < n; x++) {
			Arrays.fill(min[x], Double.POSITIVE_INFINITY);
			min[x][x] = 0;
			Arrays.fill(max[x], Double.NEGATIVE_INFINITY);
			max[x][x] = 0;
		}
		// base case
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				if(wmin[e.getIndex()] < min[e.orig()][e.dest()]) {
					// we found a better, update min and set max
					min[e.orig()][e.dest()] = wmin[e.getIndex()];
					max[e.orig()][e.dest()] = wmax[e.getIndex()];
				} else if(Cmp.eq(wmin[e.getIndex()], min[e.orig()][e.dest()])) {
					// we found an equal path, update max
					max[e.orig()][e.dest()] = Math.max(max[e.orig()][e.dest()], wmax[e.getIndex()]);
				}
			}
		}
		// general case
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					double dikj = min[i][k] + min[k][j];
					if(Cmp.le(dikj, min[i][j])) {
						// we found a better, update min and set max
						min[i][j] = dikj;
						max[i][j] = max[i][k] + max[k][j];
					} else if(Cmp.eq(dikj, min[i][j])) {
						// we found an equal path, update max
						
						max[i][j] = Math.max(max[i][j], max[i][k] + max[k][j]);
					}
					if(i == 52 && j == 130) {
						
					}
				}
			}
		}
		return new Pair<>(min, max);
	}

	public static Pair<double[][], double[][]> apspMinMin(Graph g, WeightFunction w1, WeightFunction w2) {
		return apspMinMin(g, w1.toArray(), w2.toArray());
	}

	public static Pair<double[][], double[][]> apspMinMin(Graph g, double[] w1, double[] w2) {
		int n = g.V();
		double[][] min1 = new double[n][n];
		double[][] min2 = new double[n][n];
		for(int x = 0; x < n; x++) {
			Arrays.fill(min1[x], Double.POSITIVE_INFINITY);
			min1[x][x] = 0;
			Arrays.fill(min2[x], Double.POSITIVE_INFINITY);
			min2[x][x] = 0;
		}
		// base case
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				if(w1[e.getIndex()] < min1[e.orig()][e.dest()]) {
					// we found a better, update min and set max
					min1[e.orig()][e.dest()] = w1[e.getIndex()];
					min2[e.orig()][e.dest()] = w2[e.getIndex()];
				} else if(Cmp.eq(w1[e.getIndex()], min1[e.orig()][e.dest()])) {
					// we found an equal path, update max
					min2[e.orig()][e.dest()] = Math.min(min2[e.orig()][e.dest()], w2[e.getIndex()]);
				}
			}
		}
		// general case
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					double dikj = min1[i][k] + min1[k][j];
					if(dikj < min1[i][j]) {
						// we found a better, update min and set max
						min1[i][j] = dikj;
						min2[i][j] = min2[i][k] + min2[k][j];
					} else if(Cmp.eq(dikj, min1[i][j])) {
						// we found an equal path, update max
						min2[i][j] = Math.max(min2[i][j], min2[i][k] + min2[k][j]);
					}
				}
			}
		}
		return new Pair<>(min1, min2);
	}


	/*
	 * Given a graph and two weight function wmin and wmax
	 * computes two matrices min and max such that:
	 * 
	 * min[i][j] is the cost of the shortest path from i to j wrt wmin
	 * avg[i][j] is the average cost wrt wmax of the shortest path from i to j
	 */
	public static Pair<double[][], double[][]> apspMinAvg(Graph g, double[] wmin, double[] wavg) {
		int n = g.V();
		double[][] min = new double[n][n];
		double[][] avg = new double[n][n];
		long[][] cnt = new long[n][n];
		for(int x = 0; x < n; x++) {
			Arrays.fill(min[x], Double.POSITIVE_INFINITY);
			min[x][x] = 0;
			Arrays.fill(avg[x], Double.NEGATIVE_INFINITY);
			avg[x][x] = 0;
			Arrays.fill(cnt[x], 0);
			cnt[x][x] = 1;
		}
		// base case
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				if(wmin[e.getIndex()] < min[e.orig()][e.dest()]) {
					// we found a better, update min and set max
					min[e.orig()][e.dest()] = wmin[e.getIndex()];
					avg[e.orig()][e.dest()] = wavg[e.getIndex()];
					cnt[e.orig()][e.dest()] = 1;
				} else if(Cmp.eq(wmin[e.getIndex()], min[e.orig()][e.dest()])) {
					// we found an equal path, update max
					cnt[e.orig()][e.dest()] += 1;
					avg[e.orig()][e.dest()] += (wavg[e.getIndex()] - avg[e.orig()][e.dest()]) / cnt[e.orig()][e.dest()];
				}
			}
		}
		System.out.println(ArrayExt.toString(avg));
		// general case
		for(int k = 0; k < n; k++) {
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					double dikj = min[i][k] + min[k][j];
					if(dikj < min[i][j]) {
						// we found a better, update min and set max
						min[i][j] = dikj;
						cnt[i][j] = cnt[i][k] * cnt[k][j];
						avg[i][j] = (cnt[k][j] * avg[i][k] + cnt[i][k] * avg[k][j]) / cnt[i][j];

					} else if(Cmp.eq(dikj, min[i][j])) {
						// we found an equal path, update max
						cnt[i][j] += cnt[i][k] * cnt[k][j];
						avg[i][j] += (cnt[k][j] * avg[i][k] + cnt[i][k] * avg[k][j] - avg[i][j]) / cnt[i][j];
					}
				}
			}
		}
		return new Pair<>(min, avg);
	}

	public static Pair<double[], Edge[]> dijsktra(Graph g, WeightFunction w, int orig) {
		// initialize distance array
		double[] dist = new double[g.V()];
		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		dist[orig] = 0;
		Edge[] parent = new Edge[g.V()];
		// compute shortest paths
		TreeSet<Integer> Q = new TreeSet<>(new VertexDistCmp(dist));
		Q.add(orig);
		while(!Q.isEmpty()) {
			int v = Q.pollFirst();	
			for(Edge e : g.outEdges(v)) {
				int u = e.dest();
				if(Cmp.le(dist[v] + w.getWeight(e), dist[u])) {
					Q.remove(u);
					dist[u] = dist[v] + w.getWeight(e);
					parent[u] = e;
					Q.add(u);
				}
			} 
		}	
		return new Pair<>(dist, parent);
	}
	
	public static Pair<double[], LinkedList<Edge>[]> dijsktraAllParents(Graph g, String weightLbl, int orig) {
		return dijsktraAllParents(g, g.getWeigthFunction(weightLbl), orig);
	}
	
	public static Pair<double[], LinkedList<Edge>[]> dijsktraAllParents(Graph g, WeightFunction w, int orig) {
		return dijsktraAllParents(g, w.toArray(), orig);
	}
	
	public static Pair<double[], LinkedList<Edge>[]> dijsktraAllParents(Graph g, double[] w, int orig) {
		return dijsktraAllParents(g, w, orig, null);
	}

	public static Pair<double[], LinkedList<Edge>[]> dijsktraAllParents(Graph g, double[] w, int orig, BitSet forbidden) {
		// initialize distance array
		double[] dist = new double[g.V()];
		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		dist[orig] = 0;
		@SuppressWarnings("unchecked")
		LinkedList<Edge>[] parent = new LinkedList[g.V()];
		for(int i = 0; i < g.V(); i++) {
			parent[i] = new LinkedList<>();
		}
		// compute shortest paths
		TreeSet<Integer> Q = new TreeSet<>(new VertexDistCmp(dist));
		Q.add(orig);
		while(!Q.isEmpty()) {
			int v = Q.pollFirst();	
			for(Edge e : g.outEdges(v)) {
				if(!e.isActive()) continue;
				if(forbidden != null && forbidden.get(e.getIndex())) continue;
				int u = e.dest();
				double d = dist[v] + w[e.getIndex()];
				if(Cmp.le(d, dist[u])) {
					Q.remove(u);
					dist[u] = dist[v] + w[e.getIndex()];
					parent[u].clear();
					parent[u].add(e);
					Q.add(u);
				} else if(Cmp.eq(d, dist[u])) {
					parent[u].add(e);
				}
			} 
		}	
		return new Pair<>(dist, parent);
	}
	
	public static boolean[] dijsktraECMP(Graph g, double[] w, int orig) {
		// initialize distance array
		boolean[] ecmp = new boolean[g.V()];
		double[] dist = new double[g.V()];
		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		dist[orig] = 0;
		// compute shortest paths
		TreeSet<Integer> Q = new TreeSet<>(new VertexDistCmp(dist));
		Q.add(orig);
		while(!Q.isEmpty()) {
			int v = Q.pollFirst();	
			for(Edge e : g.outEdges(v)) {
				int u = e.dest();
				double d = dist[v] + w[e.getIndex()];
				if(Cmp.le(d, dist[u])) {
					Q.remove(u);
					dist[u] = dist[v] + w[e.getIndex()];
					Q.add(u);
					ecmp[u] = ecmp[v];
				} else if(Cmp.eq(d, dist[u])) {
					ecmp[u] |= ecmp[v];
				}
			} 
		}	
		return ecmp;
	}
	
	public static Graph dijkstraDag(Graph g, String weightLbl, int orig) {
		Graph spDag = new Graph(g.V());
		LinkedList<Edge>[] parents = dijsktraAllParents(g, weightLbl, orig).second();
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : parents[v]) {
				spDag.addEdge(e);
			}
		}
		return spDag;
	}
	
	public static Graph dijkstraDag(Graph g, double[] w, int orig) {
		Graph spDag = new Graph(g.nodeLabels());
		LinkedList<Edge>[] parents = dijsktraAllParents(g, w, orig).second();
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : parents[v]) {
				spDag.addEdge(e);
			}
		}
		return spDag;
	}

	public static Graph dijkstraDag(Graph g, double[] w, int orig, int dest) {
		Graph spDagOrig = dijkstraDag(g, w, orig);
		NodeQueue Q = new NodeQueue();
		Q.add(dest);
		Graph dag = new Graph(g.nodeLabels());
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : spDagOrig.inEdges(cur)) {
				dag.addEdge(e);
				if(!Q.visited(e.orig())) {
					Q.add(e.orig());
				}
			}
		}
		return dag;
	}

	public static Path dijkstraSP(Graph g, String orig, String dest, String weightLbl) {
		return dijkstraSP(g, g.getNodeIndex(orig), g.getNodeIndex(dest), g.getWeigthFunction(weightLbl));
	}
	
	public static Path dijkstraSP(Graph g, int orig, int dest, String weightLbl) {
		return dijkstraSP(g, orig, dest, g.getWeigthFunction(weightLbl));
	}

	public static Path dijkstraSP(Graph g, int orig, int dest, WeightFunction w) {
		Pair<double[], Edge[]> spData = dijsktra(g, w, orig);
		Edge[] parent = spData.second();
		if(parent[dest] == null) return null;
		Path p = new Path(orig, dest, parent);
		p.setCost(spData.first()[dest]);
		return p;
	}
	
	public static Path dijkstraSP(Graph g, WeightFunction w, int orig, int dest) {
		Pair<double[], Edge[]> spData = dijsktra(g, w, orig);
		Edge[] parent = spData.second();
		if(parent[dest] == null) return null;
		Path p = new Path(orig, dest, parent);
		p.setCost(spData.first()[dest]);
		return p;
	}

	/*
	public static Graph shortestPathDag(Graph g, int orig, int dest, String weightLbl) {
		WeightFunction w = g.getWeigthFunction(weightLbl);
		LinkedList<Edge>[] parents = dijsktraAllParents(g, g.getWeigthFunction(weightLbl), orig).second();
		Queue<Integer> Q = new LinkedList<>();
		Q.add(dest);
		while(!Q.isEmpty()) {

		}
	}
	 */

	public static Graph shortestPathDag(Graph g, String orig, String dest, String weightLbl) {
		WeightFunction w = g.getWeigthFunction(weightLbl);
		double[][] spDist = floydWarshal(g, w);
		return shortestPathDag(g, g.getNodeIndex(orig), g.getNodeIndex(dest), spDist, w);
	}
	
	public static Graph shortestPathDag(Graph g, int orig, int dest, String weightLbl) {
		WeightFunction w = g.getWeigthFunction(weightLbl);
		double[][] spDist = floydWarshal(g, w);
		return shortestPathDag(g, orig, dest, spDist, w);
	}


	public static Graph shortestPathDagSubgraph(Graph g, int orig, double[][] spDist, WeightFunction w) {
		Graph dag = new Graph(g.nodeLabels());
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				if(Cmp.eq(spDist[orig][e.orig()] + w.getWeight(e), spDist[orig][e.dest()])) {
					dag.addEdge(e);
				}
			}
		}
		dag.setWeightFunctions(g.getWeightFunctions());
		return dag;
	}

	public static Graph shortestPathDag(Graph spDag, int dest) {
		Integer[] visited = bfsLabelReverse(spDag, dest);
		return inducedSubgraph(spDag, visited);
	}
	
	public static Graph shortestPathDag(Graph g, int orig, int dest, double[][] spDist, String weightLbl) {
		return shortestPathDag(g, orig, dest, spDist, g.getWeigthFunction(weightLbl));
	}

	public static Graph shortestPathDag(Graph g, int orig, int dest, double[][] spDist, WeightFunction w) {
		Graph dag = new Graph(g.nodeLabels());
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				if(Cmp.eq(spDist[orig][e.orig()] + w.getWeight(e) + spDist[e.dest()][dest], spDist[orig][dest])) {
					dag.addEdge(e);
				}
			}
		}
		dag.setWeightFunctions(g.getWeightFunctions());
		return dag;
	}



	public static Edge getEdge(Graph g, String orig, String dest) {
		return getEdge(g, g.getNodeIndex(orig), g.getNodeIndex(dest));
	}
	
	public static Edge getEdge(Graph g, int orig, int dest) {
		for(Edge e: g.outEdges(orig)) {
			if(e.dest() == dest) {
				return e;
			}
		}
		return null;
	}

	public static boolean hasECMP(Graph g, int orig, int dest, String weightLbl) {
		WeightFunction w = g.getWeigthFunction(weightLbl);
		return hasECMP(g, orig, dest, w.toArray());
	}
	
	public static boolean hasECMP(Graph g, int orig, int dest, double[] w) {
		LinkedList<Edge>[] parents = dijsktraAllParents(g, w, orig).second();
		Queue<Integer> Q = new LinkedList<>();
		Q.add(dest);
		Integer[] inDeg = new Integer[g.V()];
		inDeg[dest] = parents[dest].size();
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			if(inDeg[cur] > 1) return true;
			for(Edge e : parents[cur]) {
				if(inDeg[e.orig()] == null) {
					inDeg[e.orig()] = parents[e.orig()].size();
					Q.add(e.orig());
				}
			}
		}
		return false;
	}
	
	

	
	/*
	 * Make the union of two graphs with the same number of nodes.
	 */
	public static Graph union(Graph g1, Graph g2) {
		if(g1.V() != g2.V()) throw new IllegalArgumentException("the two graphs must have the same number of vertices");
		Graph g = new Graph(g1.nodeLabels());
		for(int v = 0; v < g1.V(); v++) {
			for(Edge e : g1.outEdges(v)) {
				g.addEdge(e);
			}
			for(Edge e : g2.outEdges(v)) {
				g.addEdge(e);
			}
		}
		return g;
	}
	
	public static String nodeSetToLabels(Graph g, BitSet s) {
		int v = s.nextSetBit(0);
		LinkedList<String> L = new LinkedList<>();
		while(v != -1) {
			L.add(g.getNodeLabel(v));
			v = s.nextSetBit(v + 1);
		}
		return L.toString();
	}
	
	
	public static Integer[] sccLabels(Graph g) {
		Graph gt = Graphs.transpose(g);
		DFS dfs = new DFS(gt);
		Integer[] label = new Integer[g.V()];
		for(int s : dfs.topoOrder()) {
			if(label[s] == null) {
				bfsLabel(g, s, label);
			}
		}
		return label;
	}
	
	public static int nbSCC(Graph g) {
		Integer[] sccLbl = sccLabels(g);
		int nbscc = 0;
		for(int x : sccLbl) {
			nbscc = Math.max(nbscc, x);
		}
		return nbscc + 1;
	}
	
	public static Graph largestSCC(Graph g) {
		Integer[] sccLbl = sccLabels(g);
		Count<Integer> cnt = new Count<>(sccLbl);
		Pair<Integer, Integer> lbl = cnt.mostCommon();
		boolean[] mask = ArrayExt.getEqualIndexes(sccLbl, lbl.first());
		Graph cc = inducedSubgraph(g, mask);
		cc.setName(g.getName() + "[largestcc]");
		return cc;
	}
	

	public static Bitset spReach(Graph g, int source) {
		Graph dag = Graphs.dijkstraDag(g, "igp", source);
		NodeQueue Q = new NodeQueue();
		Q.add(source);
		// perform a BFS to visit all nodes that have a unique shortest path from the source
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : dag.outEdges(cur)) {
				// check whether there is a single shortest path to the edge destination
				if(!Q.visited(e.dest()) && dag.inDeg(e.dest()) == 1) {
					Q.add(e.dest());
				}
			}
		}
		return Q.getVisitedNodes();
	}
	
	public static double diameter(Graph g, double[] w) {
		double[][] d = floydWarshal(g, w);
		double diam = Double.NEGATIVE_INFINITY;
		for(int v = 0; v < g.V(); v++) {
			for(int u = 0; u < g.V(); u++) {
				diam = Math.max(diam, d[v][u]);
			}
		}
		return diam;
	}
	

	public static boolean isComplete(Graph g, double[] w) {
		double[][] wmin = new double[g.V()][g.V()];
		for(int i = 0; i < g.V(); i++) {
			Arrays.fill(wmin[i], Double.POSITIVE_INFINITY);
		}
		for(Edge e : g.getEdgesByIndex()) {
			wmin[e.orig()][e.dest()] = Math.min(wmin[e.orig()][e.dest()], w[e.getIndex()]);
		}
		double[][] d = floydWarshal(g, w);
		for(Edge e : g.getEdgesByIndex()) {
			if(!Cmp.eq(wmin[e.orig()][e.dest()], d[e.orig()][e.dest()])) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isComplete(Graph g, WeightFunction  w) {
		return isComplete(g, w.toArray());
	}
	
	public static boolean isECMPFree(Graph g, WeightFunction wf) {
		return isECMPFree(g, wf.toArray());
	}

	public static boolean isECMPFree(Graph g, double[] w) {
		/*
		for(int v = 0; v < g.V(); v++) {
			for(int u = 0; u < g.V(); u++) {
				if(u == v) continue;
				if(hasECMP(g, v, u, w)) {
					return false;
				}
			}
		}
		return true;
		*/
		for(int v = 0; v < g.V(); v++) {
			boolean[] ecmp = dijsktraECMP(g, w, v);
			for(int u = 0; u < g.V(); u++) {
				if(ecmp[u]) return false;
			}
		}
		return true;
	}
	

	public static boolean isECMPFreeComplete(Graph g, WeightFunction w) {
		return isECMPFree(g, w) && isComplete(g, w);
	}
	
	public static boolean isECMPFreeComplete(Graph g, double[] w) {
		return isECMPFree(g, w) && isComplete(g, w);
	}

}
