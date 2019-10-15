package graph;

import dataStructures.Bitset;

public class ECMP {

	private Bitset[] ecmpFree;
	private Graph g;
	
	public ECMP(Graph g) {
		this.g = g;
		ecmpFree = new Bitset[g.V()];
		for(int v = 0; v < g.V(); v++) {
			ecmpFree[v] = Graphs.spReach(g, v);
			
		}
	}
	
	public boolean hasECMP(int v, int u) {
		return !ecmpFree[v].get(u);
	}
	
	public boolean ecmpFree(int v, int u) {
		return ecmpFree[v].get(u);
	}
	
	public boolean ecmpFree() {
		for(int v = 0; v < g.V(); v++) {
			if(ecmpFree[v].cardinality() != g.V()) {
				return false;
			}
		}
		return true;
	}

	
}
