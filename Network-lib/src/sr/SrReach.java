package sr;

import java.util.ArrayList;

import dataStructures.Bitset;
import dataStructures.Edge;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import utils.MyAssert;

public class SrReach {

	private Graph g;
	private Edge[] edges;
	private ArrayList<Bitset[]> nreach;
	private Bitset[] spreach;
	private int minSegNodeCover, minSegEdgeCover;

	public SrReach(Graph g) {
		this.g = g;
		this.edges = g.getEdgesByIndex();
		computeReach();
	}

	public int getMinSegNodeCover() {
		return minSegNodeCover;
	}

	public int getMinSegEdgeCover() {
		return minSegEdgeCover;
	}
	
	public Graph g() {
		return g;
	}

	private void computeReach0() {
		Bitset[] nreach0 = new Bitset[g.V()];
		Bitset[] ereach0 = new Bitset[g.V()];
		for(int v = 0; v < g.V(); v++) {
			nreach0[v] = new Bitset();
			ereach0[v] = new Bitset();
		}
		nreach.add(nreach0);
	}

	private void computeReach1() {
		Bitset[] nreach1 = new Bitset[g.V()];
		Bitset[] ereach1 = new Bitset[g.V()];
		for(int v = 0; v < g.V(); v++) {
			nreach1[v] = new Bitset();
			nreach1[v].set(v);
			ereach1[v] = new Bitset();
		}
		nreach.add(nreach1);
	}

	private void computeReach2() {
		spreach = new Bitset[g.V()];
		Bitset[] nreach2 = new Bitset[g.V()];
		for(int s = 0; s < g.V(); s++) {
			spreach[s] = Graphs.spReach(g, s); //computeReachDijktra(s);
			nreach2[s] = new Bitset();
			nreach2[s].or(spreach[s]);
			// build reach and parents using adjacency segments
			for(Edge e : g.outEdges(s)) {
				nreach2[s].set(e.dest());
			}
		}
		nreach.add(nreach2);
	}

	private Bitset computeReachDijktra(int source) {
		Graph dag = Graphs.dijkstraDag(g, "igp", source);
		NodeQueue Q = new NodeQueue();
		Q.add(source);
		Bitset visitedEdges = new Bitset();
		// perform a BFS to visit all nodes that have a unique shortest path from the source
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge e : dag.outEdges(cur)) {
				// check whether there is a single shortest path to the edge destination
				if(!Q.visited(e.dest()) && dag.inDeg(e.dest()) == 1) {
					visitedEdges.set(e.getIndex());
					Q.add(e.dest());
				}
			}
		}
		return Q.getVisitedNodes();
	}
	
	private boolean reachComplete() {
		int count = 0;
		for(int v = 0; v < g.V(); v++) {
			if(nreach.get(nreach.size() - 1)[v].cardinality() == g.V()) {
				count += 1;
			}
		}
		return count == g.V();
	}
	
	public Pair<Integer, Integer> getKminKmax() {
		int kmin = -1;
		int kmax = -1;
		for(int k = 0; k < nreach.size(); k++) {
			int count = 0;
			for(int v = 0; v < g.V(); v++) {
				MyAssert.assertTrue(nodeReach(k, v).cardinality() == nreach.get(k)[v].cardinality() );
				if(nodeReach(k, v).cardinality() == g.V()) {
					count += 1;
				}
			}
			if(count < g.V()) {
				kmax = k;
			}
			if(kmin == -1 && count >= 1) {
				kmin = k;
			}
			System.out.println(count);
		}
		System.out.println(kmin + " " + kmax);
		if(kmin == -1) {
			System.out.println();
			System.exit(0);
		}
		return new Pair<>(kmin, kmax);
	}


	private void computeReach() {
		nreach = new ArrayList<>();
		computeReach0();
		computeReach1();
		computeReach2(); 
		// compute reach for k = 3, 4, ...
		int k = 3;
		while(!reachComplete()) {
			Bitset[] reachNext = computeReachNext(k);
			nreach.add(reachNext);
			k++;
		}
	}

	private Bitset[] computeReachNext(int k) {
		Bitset[] nreachM1 = nreach.get(nreach.size() - 1);
		Bitset[] nreachM2 = nreach.get(nreach.size() - 2);
		// (1) compute node reach
		Bitset[] nodeReachNext = new Bitset[g.V()];
		for(int s = 0; s < g.V(); s++) {
			nodeReachNext[s] = new Bitset();
		}
		for(int s = 0; s < g.V(); s++) {	
			// if we can reach with k - 1 we can also reach with k
			nodeReachNext[s].or(nreachM1[s]);
			// update reach using node segments
			for(int v : nreachM1[s]) {
				// we can reach u using k - 1 then any node we can reach from
				// u with 1 we can reach from v with k
				nodeReachNext[s].or(spreach[v]);
			}
			// update reach using adjacency segments
			for(int v : nreachM2[s]) {
				for(Edge e : edges) {
					if(spreach[v].get(e.orig())) {
						// we can reach u with k - adjCost and we can reach
						// e.orig with one so we can reach e.dest() using
						// and adjacency segment in k: v ~~> u ~~> e
						nodeReachNext[s].set(e.dest());
					}
				}
			}
		}
		return nodeReachNext;
	}


	public SrPath computeSrPath(int orig, int dest, int k) {
		int cur = dest;
		while(cur != orig) {
			for(int v = 0; v < g.V(); v++) {

			}
		}
		return null;
	}
	
	public Bitset spReach(int v) {
		return spreach[v];
	}

	public Bitset nodeReach(int k, int v) {
		if(k >= nreach.size()) {
			return nreach.get(nreach.size() - 1)[v];
		}
		return nreach.get(k)[v];
	}
	
	
	public SrPath buildPathToNode(int s, int dest, int k) {
		SrPath p = new SrPath();
		buildPathToNode(s, dest, k, p);
		return p;
	}

	public void buildPathToNode(int s, int dest, int k, SrPath p) {
		if(k == 1) {
			p.addFirst(new Segment(s));
			return;
		}
		
		if(k == 2) {
			for(Edge e : g.outEdges(s)) {
				if(e.dest() == dest) {
					p.addFirst(new Segment(e));
					return;
				}
			}
			p.addFirst(new Segment(dest));
			p.addFirst(new Segment(s));
			return;
		}
		
		for(int v : nodeReach(k - 1, s)) {
			if(spreach[v].get(dest)) {
				p.addFirst(new Segment(dest));
				buildPathToNode(s, v, k - 1, p);
				return;
			}
		}
		
		for(int v: nodeReach(k - 2, s)) {
			for(Edge e : g.inEdges(dest)) {
				int u1 = e.orig();
				if(spreach[v].get(u1)) {
					p.addFirst(new Segment(e));
					buildPathToNode(s, v, k - 2, p);
					return;
				}
			}
		}
		
	}


	public String toString() {
		return "(" + minSegNodeCover + ", " + minSegEdgeCover + ")";
	}

}
