package monitoring;

import java.util.ArrayList;
import java.util.BitSet;

import dataStructures.Bitset;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import sr.SrPath;

public class CoverChecker {
	
	public static Pair<Boolean, String> checkCycleCover(ArrayList<SrPath> cycles, Graph g, int s, int k) {
		BitSet covered = new BitSet();
		for(int j = 0; j < cycles.size(); j++) {
			SrPath c = cycles.get(j);
			if(c.orig() != s) return new Pair<>(false, "wrong origin: " + c.orig());
			if(c.dest() != s) return new Pair<>(false, "wrong destination: " + c.dest());
			if(c.getSegmentCost() > k) return new Pair<>(false, "segment cost too high: " + c.getSegmentCost() + " > " + k);
			for(int i = 1; i < c.size(); i++) {
				Graph dag = Graphs.shortestPathDag(g, c.get(i - 1).s2(), c.get(i).s1(), "igp");
				for(int v = 0; v < dag.V(); v++) {
					if(dag.inDeg(v) > 1) {
						return new Pair<>(false, "not deterministic: " + j + ", " + c.toIndexString());
					}
				}
				covered.or(c.getEdgeSet(g));
			}
		}
		if(covered.cardinality() != g.E()) return new Pair<>(false, "does not cover all edges");
		return new Pair<>(true, "ok");
	}
	
	public static Pair<Boolean, String> checkCycleCover(MinSegCycleCover cover) {
		return checkCycleCover(cover.getCycles(), cover.getG(), cover.getSource(), cover.getMaxSeg());
	}
	
	public static Pair<Boolean, String> checkCycleCover(CycleCoverCGSolution cover) {
		return checkCycleCover(cover.getNonZeroCycles(), cover.getG(), cover.getSource(), cover.getMaxSeg());
	}


}
