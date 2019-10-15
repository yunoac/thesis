package graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dataStructures.Edge;

/**
 * Class that represents the biconnected components of a graph.
 * 
 * @author f.aubry@uclouvain.be
 */
public class BiconnectedComponents {

	private int[] num, low, parent, bcc;
	private int rootChildren, time, root;

	private boolean[] articulation;
	private HashSet<Edge> bridges;
	private int nbBridge, nbArticulation;
	
	/*
	 * Create the biconnected components.
	 */
	public BiconnectedComponents(Graph g) {
		// initialize data
		articulation = new boolean[g.V()];
		parent = new int[g.V()];
		Arrays.fill(parent, -1);
		num = new int[g.V()];
		low = new int[g.V()];
		rootChildren = 0;
		time = 1;
		bridges = new HashSet<>();
		// loop over the vertices to perform DFS
		for(int u = 0; u < g.V(); u++) {
			if(num[u] == 0) {
				// new vertex found (new connected component)
				root = u;
				rootChildren = 0;
				// compute bridges and articulation points in the connected component of u
				computeBridgesAndArticulationPoints(g, u);
				if(rootChildren > 1) {
					if(!articulation[u]) nbArticulation++;
					articulation[u] = true;
				}
			}
		}
		// compute the biconnected components of g
		computeBCC(g);
	}
	
	/*
	 * Return whether the undirected edge (u, v)
	 * is a bridge in g.
	 */
	public boolean isBridge(int u, int v) {
		return bridges.contains(new Edge(u, v));
	}
	
	/*
	 * Return whether u and v are on the same
	 * biconnected component.
	 */
	public boolean sameBcc(int u, int v) {
		return bcc[u] == bcc[v];
	}
	
	/*
	 * Check whether u is an articulation point.
	 */
	public boolean isArticulation(int u) {
		return articulation[u];
	}
	
	/*
	 * Get the number of articulation points.
	 */
	public int nbArticulation() {
		return nbArticulation;
	}
	
	/*
	 * Get the number of bridges in the graph.
	 */
	public int nbBridges() {
		return nbBridge;
	}

	/*
	 * Auxiliary method to that performs a DFS on g starting at node u
	 * to compute the articulation points and bridges.
	 */
	private void computeBridgesAndArticulationPoints(Graph g, int u) {
		num[u] = low[u] = time++;
		for(Edge e : g.outEdges(u)) {
			if(!e.isActive()) continue;
			int v = e.dest();
			if(num[v] == 0) {
				parent[v] = u;
				if(u == root) rootChildren++;
				computeBridgesAndArticulationPoints(g, v);
				if(low[v] >= num[u] && u != root) {
					if(!articulation[u]) nbArticulation++;
					articulation[u] = true;
				}
				if(low[v] > num[u]) {
					nbBridge++;
					bridges.add(new Edge(u, v));
					bridges.add(new Edge(v, u));
				}
				low[u] = Math.min(low[u], low[v]);
			} else if(v != parent[u]) { // back edge
				low[u] = Math.min(low[u], num[v]);
			}
		}
	}
	
	/*
	 * Auxiliary method to create an array bbc
	 * such that bcc[u] = bcc[v] if and only if
	 * u and v are on the same biconnected component.
	 */
	private void computeBCC(Graph g) {
		boolean[] visited = new boolean[g.V()];
		bcc = new int[g.V()];
		for(int u = 0; u < g.V(); u++) {
			if(!visited[u]) {
				Queue<Integer> Q = new LinkedList<>();
				Q.add(u);
				while(!Q.isEmpty()) {
					int cur = Q.poll();
					visited[cur] = true;
					bcc[cur] = u; 
					for(Edge e : g.outEdges(cur)) {
						if(!e.isActive()) continue;
						int v = e.dest();
						if(!visited[v] && !bridges.contains(e)) {
							Q.add(v);
						}
					}
				}
			}
		}
	}

}
