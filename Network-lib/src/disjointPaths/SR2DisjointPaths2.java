package disjointPaths;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import dataStructures.Bitset;
import dataStructures.Pair;
import dataStructures.RDPDemand;
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
import utils.Cmp;
import utils.MyAssert;

public class SR2DisjointPaths2 {

	private Graph g;
	private ForwGraphs forw;
	private double[][] minlat;
	private SrMetric srLat;

	public SR2DisjointPaths2(Graph g) {
		this.g = g;
		this.forw = new ForwGraphs(g);
		minlat = Graphs.floydWarshal(g, g.getWeigthFunction("lat"));
		srLat = new SrMetric(forw.getForwLat(), g.getWeigthFunction("lat"));
	}

	private int maxSeg;
	private RDPDemand demand;
	private SrPath p1, p2, p1Opt, p2Opt;
	private double lat1, lat2, lat1Opt, lat2Opt;
	private Bitset Ep1, Ep2;
	private long startTime;
	private TreeSet<Integer> free;
	
	private void initData() {
		p1 = new SrPath();
		p1.add(new Segment(demand.s1()));
		Ep1 = new Bitset();
		p2 = SrOptim.minWeightDisjointSrPathNoAdj(g, forw, Ep1, demand.s2(), demand.t2(), srLat, maxSeg);
		lat2 = p2.getWeight();
		Ep2 = forw.getSrPathE(p2);
		lat1Opt = Double.POSITIVE_INFINITY;
		lat2Opt = Double.POSITIVE_INFINITY;
		free = new TreeSet<>(new NodeLatCmp(minlat[demand.t1()]));
		for(int u = 0; u < g.V(); u++) {
			free.add(u);
		}
	}

	public Pair<SrPath, SrPath> computePaths(RDPDemand demand, int maxSeg) {
		this.demand = demand;
		this.maxSeg = maxSeg;
		if(MaximumEDP.computePaths(g, demand.getS(), demand.getT(), 2).size() < 2) {
			return null;
		}
		startTime = System.nanoTime();
		initData();
		BitSet Np1 = new Bitset();
		Np1.set(demand.s1());
		computePathsDFS(0, Np1, p2, Ep2, lat2);
		if(Math.max(lat1Opt, lat2Opt) == Double.POSITIVE_INFINITY) return null;
		p1Opt.setWeight(lat1Opt);
		p2Opt.setWeight(lat2Opt);
		MyAssert.assertTrue(p1Opt.getSegmentCost() <= maxSeg && p2Opt.getSegmentCost() <= maxSeg, p1Opt + "  " + p2Opt);
		MyAssert.assertTrue(!forw.getSrPathE(p1Opt).intersects(forw.getSrPathE(p2Opt)));
		return new Pair<>(p1Opt, p2Opt);
	}

	private void computePathsDFS(double lat1, BitSet Np1, SrPath p2, BitSet Ep2, double lat2) {
		// check if we can cut-off by latency
		if(Math.max(lat1 + minlat[p1.dest()][demand.t1()], lat2) >= Math.max(lat1Opt, lat2Opt)) return;
		if(p1.dest() == demand.t1()) {
			// p1 is complete, better solution found
			MyAssert.assertTrue(!Cmp.eq(lat1, 0));
			lat1Opt = lat1;
			lat2Opt = lat2;
			p1Opt = p1.copy();
			p2Opt = p2.copy();
			System.out.println("lat recuded to: " + lat1Opt + " " + lat2Opt);
		}
		if(p1.getSegmentCost() + 1 > maxSeg) return;
		for(int u : free) {
			if(Np1.get(u)) continue;
			int prev = p1.dest();
			// CYCLE CUT
			if(forw.getForwE(p1.dest(), u).intersects(Ep1)) continue;
			BitSet newNp1 = new BitSet();
			newNp1.or(Np1);
			newNp1.clear(p1.dest());
			if(forw.getForwN(p1.dest(), u).intersects(newNp1)) continue;
			// FLOW CUT
			ArrayList<Path> tmp = MaximumEDP.computPaths(g, u, demand.s2(), demand.t1(), demand.t2(), 2);
			if(tmp.size() < 2) continue;
			// update free nodes
			// update E(p1)
			Ep1.or(forw.getForwE(p1.dest(), u));
			newNp1.or(forw.getForwN(p1.dest(), u));
			// update p1
			p1.addLast(new Segment(u));
			// check whether p2 is ok with this
			if(Ep1.intersects(Ep2)) {
				// we have intersection, recompute p2
				SrPath p = SrOptim.minWeightDisjointSrPathNoAdj(g, forw, Ep1, demand.s2(), demand.t2(), srLat, maxSeg);
				if(p != null) {
					computePathsDFS(lat1 + forw.getForwLat(prev, u), newNp1, p, forw.getSrPathE(p), p.getWeight());
				}
			} else {
				// no intersection, p1 and p2 are compatible
				double l = forw.getForwLat(prev, u);
				computePathsDFS(lat1 + l, newNp1, p2, Ep2, lat2);
			}
			// backtrack p1
			p1.removeLast();
			// backtrack free nodes
			Ep1.xor(forw.getForwE(p1.dest(), u));	
		}
	}
	
	static class NodeLatCmp implements Comparator<Integer> {

		private double[] lat;
		
		public NodeLatCmp(double[] lat) {
			this.lat = lat;
		}
		
		public int compare(Integer u, Integer v) {
			int latCmp = Double.compare(lat[u], lat[v]);
			if(latCmp == 0) {
				return u - v;
			}
			return latCmp;
		}
		
		
	}
	

}
