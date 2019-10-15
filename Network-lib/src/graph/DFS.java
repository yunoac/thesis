package graph;

import dataStructures.Edge;

public class DFS {

	private int UNV = 0, OPEN = 1, CLOSED = 2;
	
	private int[] state;
	private int[] order, position;
	private int index;
	
	private Edge cycleEdge;

	public DFS(Graph g) {
		cycleEdge = null;
		state = new int[g.V()];
		order = new int[g.V()];
		index = g.V() - 1;
		// perform a dfs from each unvisited v
		for(int v = 0; v < g.V(); v++) {
			if(state[v] == UNV) {
				dfsVisit(g, v);
			}
		}
		// compute positions
		position = new int[g.V()];
		for(int i = 0; i < g.V(); i++) {
			position[order[i]] = i;
		}
	}
	
	public int[] topoOrder() {
		return order;
	}
	
	public boolean isDag() {
		return cycleEdge == null;
	}

	private void dfsVisit(Graph g, int v) {
		state[v] = OPEN;
		for(Edge e : g.outEdges(v)) {
			if(state[e.dest()] == UNV) {
				dfsVisit(g, e.dest());
			} else if(state[e.dest()] == OPEN) {
				cycleEdge = e;
			}
		}
		order[index--] = v;
		state[v] = CLOSED;
	}

}
