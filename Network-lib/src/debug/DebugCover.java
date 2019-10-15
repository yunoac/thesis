package debug;

import java.util.ArrayList;

import IO.GraphIO;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import monitoring.CoverChecker;
import monitoring.CycleCovers;
import sr.SrPath;
import sr.SrReach;

public class DebugCover {
	
/*
(false, not deterministic: 92, <34, 0, 83, 1, 34>)
nb cycles=159 k=5 s=34
<34, 0, 83, 1, 34>
34 0: false
0 83: true
83 1: false
1 34: false

 */
	
	public static void main(String[] args) {
		Graph g = GraphIO.read("./data/topologies/rf/1755.ntfl");
		SrReach r = new SrReach(g);
		int s = 34;
		int k = 5;
		
		int nonDeterministicIndex = 151;
		
		
		Edge[] edges = g.getEdgesByIndex();
		SrPath c = CycleCovers.buildCycle(r, edges[nonDeterministicIndex], s, k);
		System.out.println(c);
		for(int i = 1; i < c.size(); i++) {
			int x = c.get(i - 1).s2();
			int y = c.get(i).s1();
			System.out.println(x + " " + y + ": " + Graphs.hasECMP(g, x, y, "igp"));
			
		}
		
		
/*		
		//System.out.println(CycleCovers.existsCycleCover(r, s, k));
		
		ArrayList<SrPath> cover = new ArrayList<>();
		for(int i = nonDeterministicIndex; i < edges.length; i++) {	
			cover.add(c);
		}
		Pair<Boolean, String> res = CoverChecker.checkCycleCover(cover, g, s, k);
		System.out.println(res);
		*/
		
	}

}
