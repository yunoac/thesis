package monitoring;

import dataStructures.Edge;
import graph.Graphs;
import graph.Path;
import sr.Forw;
import sr.Segment;
import sr.SrPath;
import sr.SrReach;

public class CycleCovers {

	
	public static boolean existsCycleCover(SrReach r, Forw forw, int s, int k) {
		Edge[] edges = r.g().getEdgesByIndex();
		for(Edge e : edges) {
			if(!cycleExists(r, forw, e, s, k)) return false;
		}
		return true;
	}
	
	public static SrPath buildCycle(SrReach r, Forw forw, Edge e, int s, int k) {
		// check condition 1
		for(int k1 = 1; k1 < k; k1++) {
			int k2 = k - k1;
			for(int x : r.nodeReach(k1, s)) {
				for(int y : r.spReach(x)) {
					if(r.nodeReach(k2, y).get(s) && forw.dagContainsEdge(x, y, e)) {
						return buildCase1Cycle(r, s, x, y, k1, k2);
					}
				}
			}
		}
		// check condition 2
		int u1 = e.orig();
		int u2 = e.dest();
		for(int k1 = 1; k1 < k - 2; k1++) {
			int k2 = k - 2 - k1;
			for(int x : r.nodeReach(k1, s)) {
				if(r.spReach(x).get(u1)) {
					for(int y : r.spReach(u2)) {
						if(r.nodeReach(k2, y).get(s)) {
							// build case 2 cycle
							return buildCase2Cycle(r, s, x, y, k1, k2, e);
						}
					}
				}
			}
		}
		return null;
	}
	
	private static SrPath buildCase1Cycle(SrReach r, int s, int x, int y, int k1, int k2) {
		SrPath c = r.buildPathToNode(s, x, k1);
		for(Segment seg : r.buildPathToNode(y, s, k2)) {
			c.add(seg);
		}
		c.indexPath();
		return c;
	}
	
	private static SrPath buildCase2Cycle(SrReach r, int s, int x, int y, int k1, int k2, Edge e) {
		SrPath c = r.buildPathToNode(s, x, k1);
		c.add(new Segment(e));
		for(Segment seg : r.buildPathToNode(y, s, k2)) {
			c.add(seg);
		}
		c.indexPath();
		return c;
	}

	/*
	 * Compute whether there exists a deterministic sr-cycle from
	 * the given source node of segment cost at most maxSeg that covers edge e.
	 */
	public static boolean cycleExists(SrReach r, Forw forw, Edge e, int s, int k) {
		// check condition 1
		for(int k1 = 1; k1 < k; k1++) {
			int k2 = k - k1;
			for(int x : r.nodeReach(k1, s)) {
				for(int y : r.spReach(x)) {
					if(r.nodeReach(k2, y).get(s) && forw.dagContainsEdge(x, y, e)) {
						return true;
					}
				}
			}
		}
		// check condition 2
		int u1 = e.orig();
		int u2 = e.dest();
		for(int k1 = 1; k1 < k - 2; k1++) {
			int k2 = k - 2 - k1;
			for(int x : r.nodeReach(k1, s)) {
				if(r.spReach(x).get(u1)) {
					for(int y : r.spReach(u2)) {
						if(r.nodeReach(k2, y).get(s)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
