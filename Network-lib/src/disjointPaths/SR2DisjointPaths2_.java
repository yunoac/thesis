package disjointPaths;

import java.util.ArrayList;
import java.util.BitSet;

import dataStructures.Edge;
import dataStructures.Pair;
import dataStructures.SrMetric;
import graph.Graph;
import graph.Graphs;
import graph.MaximumEDP;
import graph.MinLatEDP;
import graph.Path;
import sr.ForwGraphs;
import sr.Segment;
import sr.SrOptim;
import sr.SrPath;
import utils.MyAssert;

public class SR2DisjointPaths2_ {

	private Graph g;
	private Edge[] edges;
	private ForwGraphs forw;
	private double[][] minlat;
	private int[][] nbEDP;
	private SrMetric srLat;

	public SR2DisjointPaths2_(Graph g) {
		this.g = g;
		edges = g.getEdgesByIndex();
		this.forw = new ForwGraphs(g);
		minlat = Graphs.floydWarshal(g, g.getWeigthFunction("lat"));
		srLat = new SrMetric(forw.getForwLat(), g.getWeigthFunction("lat"));
	}

	private int s1, t1, s2, t2, maxSeg, timeout;
	private SrPath p1, p2, p1Opt, p2Opt;
	private double lat1, lat2, lat1Opt, lat2Opt;
	private BitSet Ep1, Ep2;
	private boolean verbose;
	private BitSet usedNodes, usedEdges;
	private long startTime;
	private int[] ss, tt;
	
	private void initData(int s1, int t1, int s2, int t2, int maxSeg, BitSet Ep1, boolean verbose) {
		this.s1 = s1;
		this.t1 = t1;
		this.s2 = s2;
		this.t2 = t2;
		this.maxSeg = maxSeg;
		this.verbose = verbose;
		p2 = SrOptim.minWeightDisjointSrPath(g, forw, Ep1, s2, t2, srLat, maxSeg);
		if(p2 == null) {
			System.out.println();
		}
		lat2 = p2.getWeight();
		Ep2 = forw.getSrPathE(p2);
		lat1Opt = Double.POSITIVE_INFINITY;
		lat2Opt = Double.POSITIVE_INFINITY;
	}

	public Pair<SrPath, SrPath> computePaths(int s1, int t1, int s2, int t2, int maxSeg, int timeout, boolean verbose) {
		ss = new int[] {s1, s2};
		tt = new int[] {t1, t2};
		if(MaximumEDP.computePaths(g, ss, tt, 2).size() < 2) {
			if(verbose) System.out.println("max flow cut");
			return null;
		}
		
		this.timeout = timeout;
		startTime = System.nanoTime();
		// start with node segment
		usedNodes = new BitSet();
		usedEdges = new BitSet();
		p1 = new SrPath();
		p1.add(new Segment(s1));
		Ep1 = new BitSet();
		usedNodes.set(s1);
		initData(s1, t1, s2, t2, maxSeg, Ep1, verbose);
		computePathsDFS(Ep1, 0, p2, Ep2, lat2);

		// start with adjacency
		for(Edge e : g.outEdges(s1)) {
			usedNodes = new BitSet();
			usedEdges = new BitSet();
			p1 = new SrPath();
			p1.add(new Segment(e));
			Ep1 = new BitSet();
			Ep1.set(e.getIndex());
			usedEdges.set(e.getIndex());
			initData(s1, t1, s2, t2, maxSeg, Ep1, verbose);
			computePathsDFS(Ep1, 0, p2, Ep2, lat2);	
		}

		if(Math.max(lat1Opt, lat2Opt) == Double.POSITIVE_INFINITY) return null;
		p1Opt.setWeight(lat1);
		p2Opt.setWeight(lat2);
		MyAssert.assertTrue(p1Opt.getSegmentCost() <= maxSeg && p2Opt.getSegmentCost() <= maxSeg, p1Opt + "  " + p2Opt);
		MyAssert.assertTrue(!forw.getSrPathE(p1Opt).intersects(forw.getSrPathE(p2Opt)));
		return new Pair<>(p1Opt, p2Opt);
	}

	private void computePathsDFS(BitSet Ep1, double lat1, SrPath p2, BitSet Ep2, double lat2) {
		if((System.nanoTime() - startTime) / 1e9 > timeout) return; 
		// check if we can cut-off by latency
		if(Math.max(lat1 + minlat[p1.dest()][t1], lat2) >= Math.max(lat1Opt, lat2Opt)) return;
		if(p1.getSegmentCost() + 1 > maxSeg) return;

		if(p1.dest() == t1) {
			// p1 is complete, better solution found
			lat1Opt = lat1;
			lat2Opt = lat2;
			p1Opt = p1.copy();
			p2Opt = p2.copy();
		}
	
		// extend with a node segment
		for(int u = 0; u < g.V(); u++) {
			// CYCLE CUT
			if(usedNodes.get(u)) continue;
			BitSet E = new BitSet();
			E.or(forw.getForwE(p1.dest(), u));
			if(Ep1.intersects(E)) continue; 
			// FLOW CUT
			Pair<ArrayList<Path>, Double> tmp = MinLatEDP.computePaths(g, new int[] {u, s2}, new int[] {t1, t2}, 2);
			if(tmp.second() >= Math.max(lat1Opt, lat2Opt)) continue;
			// update used nodes
			usedNodes.set(u);
			E.or(Ep1);
			// update p1
			p1.addLast(new Segment(u));
			// check whether p2 is ok with this
			if(E.intersects(Ep2)) {
				// we have intersection, recompute p2
				SrPath p = SrOptim.minWeightDisjointSrPath(g, forw, E, s2, t2, srLat, maxSeg);
				if(p != null) {
					computePathsDFS(E, lat1 + forw.getForwLat(p1.dest(), u), p, forw.getSrPathE(p), p.getWeight());
				}
			} else {
				// no intersection, p1 and p2 are compatible
				computePathsDFS(E, lat1 + forw.getForwLat(p1.dest(), u), p2, Ep2, lat2);
			}
			// backtrack
			p1.removeLast();
			usedNodes.clear(u);
		}

		if(p1.getSegmentCost() + 2 > maxSeg) return;
		// extend with adjacency segment
		for(Edge e : edges) {
			if(usedEdges.get(e.getIndex())) continue;
			// CYCLE CUT
			BitSet E = new BitSet();
			E.or(forw.getForwE(p1.dest(), e.orig()));
			E.set(e.getIndex());
			if(Ep1.intersects(E)) continue; // cyclic sr-path
			// FLOW CUT
			if(MinLatEDP.computePaths(g, new int[] {e.dest(), s2}, new int[] {t1, t2}, 2).second() >= Math.max(lat1Opt, lat2Opt)) continue;
			// update used edges
			usedEdges.set(e.getIndex());
			E.or(Ep1);
			// update p1
			p1.addLast(new Segment(e));
			// check whether p2 is ok with this
			if(E.intersects(Ep2)) {
				// we have intersection, recompute p2
				SrPath p = SrOptim.minWeightDisjointSrPath(g, forw, E, s2, t2, srLat, maxSeg);
				if(p != null) {
					computePathsDFS(E, lat1 + g.getWeight("lat", e), p, forw.getSrPathE(p), p.getWeight());
				}
			} else {
				// no intersection, p1 and p2 are compatible
				computePathsDFS(E, lat1 + g.getWeight("lat", e), p2, Ep2, lat2);
			}
			// backtrack
			p1.removeLast();
			usedEdges.clear(e.getIndex());
		}
	}
	

}
