package monitoring;

import java.util.ArrayList;

import dataStructures.Bitset;
import dataStructures.Edge;
import graph.Graph;
import sr.Forw;
import sr.SrPath;
import sr.SrReach;
import utils.MyAssert;

public class MinSegCycleCover {

	private Graph g;
	private Forw forw;
	private SrReach r;
	private int source, k;
	private ArrayList<SrPath> cycles;
	private ArrayList<Edge> cycleEdge;
	private boolean verbose;

	public MinSegCycleCover(Graph g, boolean verbose) {
		this.g = g;
		this.verbose = verbose;
		forw = new Forw(g);
		r = new SrReach(g);
		computeSegSource();
		computeCycles();
	}
	
	public MinSegCycleCover(Graph g, int source, int k, boolean verbose) {
		this.g = g;
		forw = new Forw(g);
		this.source = source;
		this.k = k;
		this.verbose = verbose;
		r = new SrReach(g);
		computeCycles();
	}
	
	public Graph getG() {
		return g;
	}
	
	private void computeSegSource() {
		if(verbose) System.out.println("finding k and s");
		k = r.getMinSegNodeCover();
		source = -1;
		while(source == -1) {
			for(int s = 0; source == -1 && s < g.V(); s++) {
				if(verbose) System.out.println("trying k=" + k + ", s=" + s);
				if(CycleCovers.existsCycleCover(r, forw, s, k)) {
					source = s;
				}
			}
			if(source == -1) k += 1;
		}
	}
	
	private void computeCycles() {
		// compute the cycles
		cycles = new ArrayList<>();
		cycleEdge = new ArrayList<>();
		Bitset covered = new Bitset();
		Edge[] edges = g.getEdgesByIndex();
		if(verbose) System.out.println("finding cycles");
		for(int i = 0; i < edges.length; i++) {
			Edge e = edges[i];
			if(verbose) System.out.println((int)((100.0 * i) / edges.length) + "%");
			if(!covered.get(e.getIndex())) {
				SrPath c = CycleCovers.buildCycle(r, forw, e, source, k);
				MyAssert.assertTrue(c.contains(e, g), "" + i);
				cycles.add(c);
				cycleEdge.add(e);
				// mark new edges as covered
				for(Edge ee : edges) {
					if(!covered.get(ee.getIndex()) && c.contains(ee, g)) {
						covered.set(ee.getIndex());
					}
				}
			}
		}
	}

	public Edge getCycleEdge(int i) {
		return cycleEdge.get(i);
	}

	public int getSource() {
		return source;
	}

	public int getMaxSeg() {
		return k;
	}

	public ArrayList<SrPath> getCycles() {
		return cycles;
	}

	public int nbCycles() {
		return cycles.size();
	}

}
