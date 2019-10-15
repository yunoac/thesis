package monitoring;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;

import graph.Graph;
import sr.SrPath;
import utils.MyAssert;

public class CycleCoverRounder {

	public static ArrayList<SrPath> greedyRounder(Graph g, ArrayList<SrPath> cycles) {
		ArrayList<Cycle> C = new ArrayList<>();
		for(int i = 0; i < cycles.size(); i++) {
			C.add(new Cycle(g, cycles.get(i), i));
		}
		BitSet covered = new BitSet();
		BitSet taken = new BitSet();
		while(covered.cardinality() < g.E()) {
			Collections.sort(C, new CoverCmp(g, covered));
			MyAssert.assertTrue(!taken.get(C.get(0).index));
			taken.set(C.get(0).index);
			covered.or(C.get(0).c.getEdgeSet(g));
		}
		ArrayList<SrPath> cover = new ArrayList<>();
		for(int i = 0; i < cycles.size(); i++) {
			if(taken.get(i)) {
				cover.add(cycles.get(i));
			}
		}
		return cover;
	}
	
	public static class Cycle {
		
		public SrPath c;
		public int index;
		public BitSet e;
		
		public Cycle(Graph g, SrPath c, int index) {
			this.c = c;
			this.index = index;
			e = c.getEdgeSet(g);
		}

	}
	
	public static class CoverCmp implements Comparator<Cycle> {
		
		private BitSet covered;
		private Graph g;
		
		public CoverCmp(Graph g, BitSet covered) {
			this.g = g;
			this.covered = covered;
		}

		public int compare(Cycle c1, Cycle c2) {
			BitSet e1 = new BitSet();
			e1.or(c1.e);
			e1.or(covered);
			BitSet e2 = new BitSet();
			e2.or(c2.e);
			e2.or(covered);
			return -(e1.cardinality() - e2.cardinality());
		}
		
	}
	
		
}
