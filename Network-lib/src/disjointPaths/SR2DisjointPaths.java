package disjointPaths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import dataStructures.Bitset;
import dataStructures.Edge;
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

public class SR2DisjointPaths {

	private Graph g;
	private ForwGraphs forw;
	private double[][] minlat;
	private SrMetric srLat;

	public SR2DisjointPaths(Graph g) {
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
	private Segment[] free;
	
	private void initData() {
		p1 = new SrPath();
		p1.add(new Segment(demand.s1()));
		Ep1 = new Bitset();
		p2 = SrOptim.minWeightDisjointSrPathNoAdj(g, forw, Ep1, demand.s2(), demand.t2(), srLat, maxSeg);
		lat2 = p2.getWeight();
		Ep2 = forw.getSrPathE(p2);
		
	}

	public Pair<SrPath, SrPath> computePaths(RDPDemand demand, int maxSeg) {
		this.demand = demand;
		this.maxSeg = maxSeg;
		if(MaximumEDP.computePaths(g, demand.getS(), demand.getT(), 2).size() < 2) {
			return null;
		}
		startTime = System.nanoTime();
		// build possible starting segments for p1
		Segment[] initialSegs = new Segment[1 + g.outDeg(demand.s1())];
		initialSegs[0] = new Segment(demand.s1());
		int i = 1;
		for(Edge e : g.outEdges(demand.s1())) {
			initialSegs[i++] = new Segment(e);
		}
		// initialize general data
		p1Opt = null;
		lat1Opt = Double.POSITIVE_INFINITY;
		p2Opt = null;
		lat2Opt = Double.POSITIVE_INFINITY;
		free = new Segment[g.V() + g.E()];
		i = 0;
		for(int u = 0; u < g.V(); u++) {
			free[i++] = new Segment(u);
		}
		for(Edge e : g.getEdgesByIndex()) {
			free[i++] = new Segment(e);
		}
		Arrays.sort(free, new SegmentLatCmp(minlat[demand.t1()], g.getWeigthFunction("lat").toArray()));
		for(Segment s : initialSegs) {
			// initialize p1
			p1 = new SrPath();
			p1.add(s);
			BitSet Np1 = new BitSet();
			Np1.set(s.s1());
			Ep1 = new Bitset();
			if(s.isAdj()) {
				Ep1.set(s.getEdge().getIndex());
				Np1.set(s.s2());
			}
			// initialize p2
			p2 = SrOptim.minWeightDisjointSrPath(g, forw, Ep1, demand.s2(), demand.t2(), srLat, maxSeg);
			if(p2 == null) continue;
			lat2 = p2.getWeight();
			Ep2 = forw.getSrPathE(p2);
			computePathsDFS(s.isAdj() ? g.getWeight("lat", s.getEdge()) : 0, Np1, p2, Ep2, lat2);	
		}
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
		for(Segment s : free) {
			if(p1.getSegmentCost() + s.getSegmentCost() > maxSeg) continue;
			int prev = p1.dest();
			// Check cuts
			// CYCLE CUT
			if(Np1.get(s.s1()) || Np1.get(s.s2())) continue;
			if(s.isAdj() && Ep1.get(s.getEdge().getIndex())) continue;
			if(forw.getForwE(prev, s.s1()).intersects(Ep1)) continue;
			BitSet newNp1 = new BitSet();
			newNp1.or(Np1);
			newNp1.clear(prev);
			if(forw.getForwN(prev, s.s1()).intersects(newNp1)) continue;
			// FLOW CUT
			ArrayList<Path> tmp = MaximumEDP.computPaths(g, s.s2(), demand.s2(), demand.t1(), demand.t2(), 2);
			if(tmp.size() < 2) continue;
			// update free nodes
			// update E(p1)
			Ep1.or(forw.getForwE(prev, s.s1()));
			newNp1.or(forw.getForwN(prev, s.s1()));
			if(s.isAdj()) {
				Ep1.set(s.getEdge().getIndex());
				newNp1.set(s.s2());
			}
			// update p1
			p1.addLast(s);
			// check whether p2 is ok with this
			double l = forw.getForwLat(prev, s.s1()) + (s.isAdj() ? g.getWeight("lat", s.getEdge()) : 0);
			if(Ep1.intersects(Ep2)) {
				// we have intersection, recompute p2
				SrPath p = SrOptim.minWeightDisjointSrPath(g, forw, Ep1, demand.s2(), demand.t2(), srLat, maxSeg);
				if(p != null) {
					computePathsDFS(lat1 + l, newNp1, p, forw.getSrPathE(p), p.getWeight());
				}
			} else {
				// no intersection, p1 and p2 are compatible
				computePathsDFS(lat1 + l, newNp1, p2, Ep2, lat2);
			}
			// backtrack p1
			p1.removeLast();
			// backtrack free nodes
			Ep1.xor(forw.getForwE(p1.dest(), s.s1()));
			if(s.isAdj()) {
				Ep1.clear(s.getEdge().getIndex());
			}
		}
	}
	
	static class SegmentLatCmp implements Comparator<Segment> {

		private double[] lat, edgeLat;
		
		public SegmentLatCmp(double[] lat, double[] edgeLat) {
			this.lat = lat;
			this.edgeLat = edgeLat;
		}
		
		public int compare(Integer u, Integer v) {
			int latCmp = Double.compare(lat[u], lat[v]);
			if(latCmp == 0) {
				return u - v;
			}
			return latCmp;
		}

		public int compare(Segment x, Segment y) {
			int latCmp = Double.compare(x.s2() + (x.isAdj() ? edgeLat[x.getEdge().getIndex()] : 0), y.s2() + (y.isAdj() ? edgeLat[y.getEdge().getIndex()] : 0));
			if(latCmp == 0) {
				if(x.isAdj() && y.isAdj()) return x.getEdge().getIndex() - y.getEdge().getIndex();
				if(y.isAdj()) return -1;
				if(x.isAdj()) return 1;
				return x.s1() - y.s1();
			}
			return latCmp;
		}
		
		
	}
	

}
