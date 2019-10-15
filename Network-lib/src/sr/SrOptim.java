package sr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;

import dataStructures.Edge;
import dataStructures.IntPair;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import dataStructures.SrMetric;
import graph.ECMP;
import graph.Graph;
import graph.Graphs;
import graph.WeightFunction;
import utils.Cmp;

public class SrOptim {

	public static SrPath minWeightDisjointSrPathNoAdj(Graph g, ForwGraphs forw, BitSet forbidden, int orig, int dest, SrMetric w, int maxSeg) {
		maxSeg -= 1;
		// cost[s][x] = min cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.POSITIVE_INFINITY);
		}
		sol[0][orig] = 0;
		// initialize the parents to rebuild the solution
		Segment[][] parent = new Segment[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		for(int i = 1; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					if(forw.getForwE(prev, cur).intersects(forbidden)) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.POSITIVE_INFINITY) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l < sol[i][cur]) {
						sol[i][cur] = l;
						parent[i][cur] = new Segment(prev);
					}
				}
			}
		}
		if(parent[maxSeg][dest] == null) return null;
		double opt = sol[maxSeg][dest];
		int seg = maxSeg;
		while(seg - 1 >= 0 && Cmp.eq(sol[seg - 1][dest], opt)) {
			seg -= 1;
		}
		SrPath p = new SrPath(parent, orig, dest, seg);
		p.setWeight(opt);
		return p;
	}


	public static SrPath minWeightDisjointSrPath(Graph g, ForwGraphs forw, BitSet forbidden, int orig, int dest, SrMetric w, int maxSeg) {
		//maxSeg -= 1;
		// cost[s][x] = min cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.POSITIVE_INFINITY);
		}
		sol[1][orig] = 0;
		// initialize the parents to rebuild the solution
		Parent2[][] parent = new Parent2[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		for(int i = 2; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					if(forw.getForwE(prev, cur).intersects(forbidden)) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.POSITIVE_INFINITY) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l < sol[i][cur]) {
						sol[i][cur] = l;
						parent[i][cur] = new Parent2(prev, null);
					}
				}
				// check for adjacency segments
				if(i < 2) continue;
				for(Edge e : g.inEdges(cur)) {
					if(forbidden.get(e.getIndex())) continue;
					for(int prev = 0; prev < g.V(); prev++) {
						if(prev == cur) continue;
						if(forw.getForwE(prev, e.orig()).intersects(forbidden)) continue;
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(w.getWeight(prev, e.orig()) == Double.POSITIVE_INFINITY) continue;
						double l = sol[i - 2][prev] + w.getWeight(prev, e.orig()) + w.getWeight(e);
						if(l < sol[i][cur]) {
							sol[i][cur] = l;
							parent[i][cur] = new Parent2(prev, e);
						}
					}
				}
			}
		}		
		if(parent[maxSeg][dest] == null) return null;
		double opt = sol[maxSeg][dest];
		int seg = maxSeg;
		while(seg - 1 >= 0 && Cmp.eq(sol[seg - 1][dest], opt)) {
			seg -= 1;
		}
		if(forbidden.cardinality() == 1 && forbidden.get(160)) {
			System.out.println();
		}
		LinkedList<Segment> segList = new LinkedList<>();
		Parent2 cur = parent[seg][dest];
		while(cur != null) {
			if(cur.edge == null) {
				segList.addFirst(new Segment(cur.node));
				seg -= 1;
			} else {
				if(!segList.isEmpty()) {
					Segment first = segList.getFirst();
					if(first.isNode() && first.s1() == cur.edge.dest()) {
						segList.removeFirst();
					}
				}
				segList.addFirst(new Segment(cur.edge));
				segList.addFirst(new Segment(cur.node));
				seg -= 2;
			}
			cur = parent[seg][cur.node];
		}
		if(segList.isEmpty() || segList.getFirst().s1() != orig) segList.addFirst(new Segment(orig));
		if(segList.getLast().s2() != dest) segList.addLast(new Segment(dest));
		SrPath p = new SrPath();
		for(Segment s : segList) {
			p.add(s);
		}
		p.setWeight(opt);
		p.indexPath();
		return p;
	}

	static class Parent2 {

		Integer node;
		Edge edge;

		public Parent2(Integer node, Edge edge) {
			this.node = node;
			this.edge = edge;
		}

		public String toString() {
			return node + " " + edge;
		}

	}


	public static SrMetric latMetric(Graph g) {
		ForwGraphs forw = new ForwGraphs(g);
		return new SrMetric(forw.getForwLat(), g.getWeigthFunction("lat"));
	}

	/*
	 * Compute a matrix A such that A[u][v] is equal to the
	 * maximum cost of a shortest igp path from u to v with 
	 * respect to the given cost metric
	 * 
	 * g: the graph corresponding to the network topology
	 * 
	 * igpMetric: the label of the weight function in g that represents igp weights
	 * 
	 * costMetric: the label of the weight function in g that represents the link costs
	 */
	public static double[][] minMaxSegWeightSrPath(Graph g, String igpMetric, String costMetric) {
		double[] igp = g.getWeigthFunction(igpMetric).toArray();
		double[] cost = g.getWeigthFunction(costMetric).toArray();
		return Graphs.apspMinMax(g, igp, cost).second();
	}

	/*
	 * Computes a sr path from orig to dest that minimizes the total cost of the path
	 * It assumes that no information is known about which IGP shortest path is used
	 * and therefore assumes that the worst of them is used with respect to the cost matrix
	 * 
	 * g: the graph corresponding to the network topology
	 * 
	 * orig: the origin of the path
	 * 
	 * dest: the destination of the path
	 *
	 * segCost: A matrix such that segCost[u][v] represents the cost of forwarding packets
	 * from node u to node v using shortest path routing
	 * 
	 * costMetric: a weight function representing the cost of using an adjacency segment
	 * 
	 * maxSeg: the maximum number of segments allowed
	 * 
	 * adjCost: the cost of using an adjacency segment. Set this value to anything larger
	 * than maxSeg to prevet using adjacency segments. It can be Integer.MAX_VALUE, there is
	 * no overflow danger
	 */
	public static SrPath minMaxCostSrPath(Graph g, int orig, int dest, String igpMetric, String costMetric, int maxSeg, int adjCost) {
		double[][] segCost = minMaxSegWeightSrPath(g, igpMetric, costMetric);
		return minWeightSrPath(g, orig, dest, segCost, g.getWeigthFunction(costMetric), maxSeg, adjCost);
	}

	/*
	 * Computes a sr path from orig to dest that minimizes the total cost of the path
	 * 
	 * g: the graph corresponding to the network topology
	 * 
	 * orig: the origin of the path
	 * 
	 * dest: the destination of the path
	 *
	 * igpMetric: the label of the weight function in g that represents igp weights
	 * 
	 * costMetric: the label of the weight function in g that represents the link costs
	 * 
	 * maxSeg: the maximum number of segments allowed
	 * 
	 * adjCost: the cost of using an adjacency segment. Set this value to anything larger
	 * than maxSeg to prevet using adjacency segments. It can be Integer.MAX_VALUE, there is
	 * no overflow danger
	 */
	public static SrPath minWeightSrPath(Graph g, int orig, int dest, double[][] segCost, WeightFunction edgeCost, int maxSeg, int adjCost) {
		return minWeightSrPath(g, orig, dest, new SrMetric(segCost, edgeCost), maxSeg, adjCost);
	}

	public static SrPath minWeightSrPath(Graph g, int orig, int dest, SrMetric w, int maxSeg) {
		return minWeightSrPath(g, orig, dest, w, maxSeg, 2);
	}

	public static SrPath minWeightSrPath(Graph g, int orig, int dest, SrMetric w, int maxSeg, int adjCost) {
		// cost[s][x] = min cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.POSITIVE_INFINITY);
		}
		sol[0][orig] = 0;
		// initialize the parents to rebuild the solution
		Segment[][] parent = new Segment[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		for(int i = 1; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.POSITIVE_INFINITY) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l < sol[i][cur]) {
						sol[i][cur] = l;
						parent[i][cur] = new Segment(prev);
					}
				}
				// check for adjacency segments
				if(i < adjCost) continue;
				for(Edge e : g.inEdges(cur)) {
					for(int prev = 0; prev < g.V(); prev++) {
						if(prev == cur) continue;
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(w.getWeight(prev, e.orig()) == Double.POSITIVE_INFINITY) continue;
						double l = sol[i - adjCost][prev] + w.getWeight(prev, e.orig()) + w.getWeight(e);
						if(l < sol[i][cur]) {
							sol[i][cur] = l;
							parent[i][cur] = new Segment(e);
						}
					}
				}
			}
		}
		double opt = sol[maxSeg][dest];
		int seg = maxSeg;
		while(seg - 1 >= 0 && Cmp.eq(sol[seg - 1][dest], opt)) {
			seg -= 1;
		}
		SrPath p = new SrPath(parent, orig, dest, seg);
		p.setWeight(opt);
		return p;
	}

	public static SrPath maxWeightSrPath(Graph g, int orig, int dest, SrMetric w, int maxSeg) {
		return maxWeightSrPath(g, orig, dest, w, maxSeg, 2);
	}

	public static SrPath maxWeightSrPath(Graph g, int orig, int dest, SrMetric w, int maxSeg, int adjCost) {
		// sol[s][x] = max cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.NEGATIVE_INFINITY);
		}
		sol[0][orig] = 0;
		// initialize the parents to rebuild the solution
		Segment[][] parent = new Segment[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		for(int i = 1; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.NEGATIVE_INFINITY) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l > sol[i][cur]) {
						sol[i][cur] = l;
						parent[i][cur] = new Segment(prev);
					}
				}
				// check for adjacency segments
				if(i < adjCost) continue;
				for(Edge e : g.inEdges(cur)) {
					for(int prev = 0; prev < g.V(); prev++) {
						if(prev == cur) continue;
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(w.getWeight(prev, e.orig()) == Double.NEGATIVE_INFINITY) continue;
						double l = sol[i - adjCost][prev] + w.getWeight(prev, e.orig()) + w.getWeight(e);
						if(l > sol[i][cur]) {
							sol[i][cur] = l;
							parent[i][cur] = new Segment(e);
						}
					}
				}
			}
		}
		double opt = sol[maxSeg][dest];
		int seg = maxSeg;
		while(seg - 1 >= 0 && Cmp.eq(sol[seg - 1][dest], opt)) {
			seg -= 1;
		}
		SrPath p = new SrPath(parent, orig, dest, seg);
		p.setWeight(opt);
		return p;
	}


	public static SrPath maxWeightDetSrPath(Graph g, ECMP ecmp, int orig, int dest, SrMetric w, int maxSeg) {
		return maxWeightDetSrPath(g, ecmp, orig, dest, w, maxSeg, 2);
	}

	public static SrPath maxWeightDetSrPath(Graph g, ECMP ecmp, int orig, int dest, SrMetric w, int maxSeg, int adjCost) {
		// sol[s][x] = max cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.NEGATIVE_INFINITY);
		}
		sol[0][orig] = 0;
		// initialize the parents to rebuild the solution
		Parent[][] parent = new Parent[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		for(int i = 1; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.NEGATIVE_INFINITY || ecmp.hasECMP(prev, cur)) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l > sol[i][cur]) {
						sol[i][cur] = l;
						parent[i][cur] = new Parent(prev);
					}
				}
				// check for adjacency segments
				if(i < adjCost) continue;
				for(Edge e : g.inEdges(cur)) {
					for(int prev = 0; prev < g.V(); prev++) {
						if(prev == cur) continue;
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(w.getWeight(prev, e.orig()) == Double.NEGATIVE_INFINITY || ecmp.hasECMP(prev, e.orig())) continue;
						double l = sol[i - adjCost][prev] + w.getWeight(prev, e.orig()) + w.getWeight(e);
						if(l > sol[i][cur]) {
							sol[i][cur] = l;
							parent[i][cur] = new Parent(prev, e);
						}
					}
				}
			}
		}
		double opt = sol[maxSeg][dest];
		int seg = maxSeg;
		while(seg - 1 >= 0 && Cmp.eq(sol[seg - 1][dest], opt)) {
			seg -= 1;
		}		
		//SrPath p = new SrPath(parent, orig, dest, seg);
		SrPath p = new SrPath();
		buildPath(parent, seg, dest, p);
		p.indexPath();
		p.setWeight(opt);
		return p;
	}



	private static void buildPath(Parent[][] parent, int k, int v, SrPath p) {
		if(parent[k][v] == null) {
			if(p.getFirst().s1() != v) {
				p.addFirst(v);
			}
		} else {
			if(parent[k][v].edge == null) {
				p.addFirst(v);
				buildPath(parent, k - 1, parent[k][v].node, p);
			} else {
				p.addFirst(parent[k][v].edge);
				buildPath(parent, k - 2, parent[k][v].node, p);
			}
		}
	}

	private static class Parent {

		private int node;
		private Edge edge;

		public Parent(int node) {
			this.node = node;
		}

		public Parent(int node, Edge e) {
			this.node = node;
			this.edge = e;
		}

	}

	public static SrPath maxCapSrPath(Graph g, ForwGraphs forw, int orig, int dest, int maxSeg) {
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], 0);
		}
		sol[0][orig] = Double.POSITIVE_INFINITY;
		// initialize the parents to rebuild the solution
		Segment[][] parent = new Segment[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		for(int i = 1; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					// check whether there is a path from prev to cur in the original graph
					double cap = Math.min(sol[i - 1][prev], forw.getForwCap(prev, cur));
					if(cap > sol[i][cur]) {
						sol[i][cur] = cap;
						parent[i][cur] = new Segment(prev);
					}
				}
				// check for adjacency segments
				if(i < 2) continue;
				for(Edge e : g.inEdges(cur)) {
					for(int prev = 0; prev < g.V(); prev++) {
						if(prev == cur) continue;
						double cap = Math.min(Math.min(sol[i - 2][prev], forw.getForwCap(prev, e.orig())), g.getWeight("bdw", e));
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(cap > sol[i][cur]) {
							sol[i][cur] = cap;
							parent[i][cur] = new Segment(e);
						}
					}
				}
			}
		}
		double opt = sol[maxSeg][dest];
		int seg = maxSeg;
		while(seg - 1 >= 0 && Cmp.eq(sol[seg - 1][dest], opt)) {
			seg -= 1;
		}
		SrPath p = new SrPath(parent, orig, dest, seg);
		p.setWeight(opt);
		return p;
	}


	/***
	 *              
	 *                
	 * EXPERIMENTAL \  /
	 *               \/
	 */

	public static double[][] minWeightSrPathMatrix(Graph g, int orig, SrMetric w, int maxSeg, int adjCost) {
		// cost[s][x] = min cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.POSITIVE_INFINITY);
		}
		sol[0][orig] = 0;
		// initialize the parents to rebuild the solution
		// loop of i and x to compute to fill in dist
		for(int i = 1; i <= maxSeg; i++) {
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev = 0; prev < g.V(); prev++) {
					if(prev == cur) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.POSITIVE_INFINITY) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l < sol[i][cur]) {
						sol[i][cur] = l;
					}
				}
				// check for adjacency segments
				if(i < adjCost) continue;
				for(Edge e : g.inEdges(cur)) {
					for(int prev = 0; prev < g.V(); prev++) {
						if(prev == cur) continue;
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(w.getWeight(prev, e.orig()) == Double.POSITIVE_INFINITY) continue;
						double l = sol[i - adjCost][prev] + w.getWeight(prev, e.orig()) + w.getWeight(e);
						if(l < sol[i][cur]) {
							sol[i][cur] = l;
						}
					}
				}
			}
		}
		return sol;
	}

	public static double[][] minWeightSrPathFaster2(Graph g, int orig, SrMetric w, int maxSeg, int adjCost) {
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.POSITIVE_INFINITY);
		}
		sol[0][orig] = 0;
		Queue<IntPair> Q = new LinkedList<>();
		boolean[][] inQ = new boolean[maxSeg + 1][g.V()];
		IntPair src = new IntPair(0, orig);
		Q.add(src);
		inQ[0][orig] = true;
		while(!Q.isEmpty()) {
			// CASE 1
			// remove element from queue
			IntPair cur = Q.poll();
			inQ[cur.x()][cur.y()] = false;
			// do not use new segments
			IntPair next = new IntPair(cur.x() + 1, cur.y());
			if(next.x() <= maxSeg && sol[cur.x()][cur.y()] < sol[next.x()][next.y()]) {
				sol[next.x()][next.y()] = sol[cur.x()][cur.y()];
				if(!inQ[next.x()][next.y()]) {
					Q.add(next);
					inQ[next.x()][next.y()] = true;
				}
			}
			// CASE 2
			for(int v = 0; v < g.V(); v++) {
				// use a node segment on v
				next = new IntPair(cur.x() + 1, v);
				if(next.x() <= maxSeg && sol[cur.x()][cur.y()] + w.getWeight(cur.y(), next.y()) < sol[next.x()][next.y()]) {
					sol[next.x()][next.y()] = sol[cur.x()][cur.y()] + w.getWeight(cur.y(), next.y());
					if(!inQ[next.x()][next.y()]) {
						Q.add(next);
						inQ[next.x()][next.y()] = true;
					}	
				}
			}
			// CASE 3
			for(Edge e : g.getEdgesByIndex()) {
				// use an adjacency segment on e
				next = new IntPair(cur.x() + 2, e.dest());
				if(next.x() <= maxSeg && sol[cur.x()][cur.y()] + w.getWeight(cur.y(), e.orig()) + w.getWeight(e) < sol[next.x()][next.y()]) {
					sol[next.x()][next.y()] = sol[cur.x()][cur.y()] + w.getWeight(cur.y(), e.orig()) + w.getWeight(e);
					if(!inQ[next.x()][next.y()]) {
						Q.add(next);
						inQ[next.x()][next.y()] = true;
					}	
				}
			}
		}
		return sol;
	}


	public static double[][] minWeightSrPathFaster(Graph g, int orig, SrMetric w, int maxSeg, int adjCost) {
		// cost[s][x] = min cost of a sr path to x using at most s segments
		double[][] sol = new double[maxSeg + 1][g.V()];
		for(int i = 0; i < sol.length; i++) {
			Arrays.fill(sol[i], Double.POSITIVE_INFINITY);
		}
		sol[0][orig] = 0;
		// initialize the parents to rebuild the solution
		Segment[][] parent = new Segment[maxSeg + 1][g.V()];
		// loop of i and x to compute to fill in dist
		NodeQueue Q = new NodeQueue();
		Q.add(orig);
		for(int i = 1; i <= maxSeg; i++) {
			if(Q.isEmpty()) break;
			NodeQueue N = new NodeQueue();
			for(int cur = 0; cur < g.V(); cur++) {
				// no not use a new segment (1)
				sol[i][cur] = sol[i - 1][cur];
				parent[i][cur] = parent[i - 1][cur];
				// can we reach cur by coming from prev (2)
				for(int prev : Q) {
					if(prev == cur) continue;
					// check whether there is a path from prev to cur in the original graph
					if(w.getWeight(prev, cur) == Double.NEGATIVE_INFINITY) continue;
					double l = sol[i - 1][prev] + w.getWeight(prev, cur);
					if(l < sol[i][cur]) {
						sol[i][cur] = l;
						parent[i][cur] = new Segment(prev);
						N.add(cur);
					}
				}
				// check for adjacency segments
				if(i < adjCost) continue;
				for(Edge e : g.inEdges(cur)) {
					//for(int prev : Q) {
					for(int prev = 0; prev < g.V(); prev++) {

						if(prev == cur) continue;
						// go to prev with at most i - adjCost segments and then add an adjacecy segment on e
						if(w.getWeight(prev, e.orig()) == Double.NEGATIVE_INFINITY) continue;
						double l = sol[i - adjCost][prev] + w.getWeight(prev, e.orig()) + w.getWeight(e);
						if(l < sol[i][cur]) {
							sol[i][cur] = l;
							parent[i][cur] = new Segment(e);
							N.add(cur);
						}
					}
				}
			}
			Q = N;
		}
		return sol;
	}

	private static void buildPath(Segment[][] parent, int orig, int dest, int nbSeg, double[][] cost) {
		SrPath p = new SrPath();
		Segment cur = parent[nbSeg][dest];
		while(cur != null) {
			System.out.println(nbSeg + " " + dest + ": " + cost[nbSeg][dest]);
			p.addFirst(cur);
			nbSeg -= cur.isAdj() ? 2 : 1;
			dest = cur.s1();
			cur = parent[nbSeg][dest];
		}
		if(p.size() == 0 || p.getFirst().s1() != orig) p.addFirst(new Segment(orig));
		if(p.getLast().s2() != dest) p.addLast(new Segment(dest));
		p.indexPath();
	}



}
