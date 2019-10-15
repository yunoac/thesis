package monitoring;

import dataStructures.Edge;
import graph.Graph;
import graph.Graphs;
import graph.Path;
import sr.Segment;
import sr.SrPath;

public class Monitoring2 {
	
	public static Edge findFaultyEdge(Graph g, SrPath c) {
		int lastOk = segmentSearch(g, c);
		Path p = Graphs.dijkstraSP(g, c.get(lastOk).s2(), c.get(lastOk + 1).s1(), "igp");
		int L = 0;
		int R = p.V();
		while(R - L >= 2) {
			int M = (L + R) / 2;
			SrPath probePath = new SrPath();
			for(int i = 0; i <= lastOk; i++) probePath.add(c.get(i));
			probePath.add(new Segment(p.getNode(M)));
			for(int i = lastOk; i >= 0; i--) probePath.add(c.get(i));
			boolean ok = sendProbe(g, c);
			if(ok) L = M;
			else R = M;
		}
		if(L == p.V() - 1) return c.get(lastOk + 1).getEdge();
		return p.getEdge(L);
	}
	
	private static int segmentSearch(Graph g, SrPath c) {
		int L = 0;
		int R = c.size();
		while(R - L >= 2) {
			int M = (L + R) / 2;
			SrPath probePath = new SrPath();
			for(int i = 0; i <= M; i++) probePath.add(c.get(i));
			for(int i = M - 1; i >= 0; i--) probePath.add(c.get(i));
			boolean ok = sendProbe(g, c);
			if(ok) 
				L = M;
			else R = M; 
		}
		return L;
	}
	
	private static boolean sendProbe(Graph g, SrPath c) {
		for(int i = 0; i < c.size(); i++) {
			if(!c.get(i).isAdj() && c.get(i).getEdge().isActive()) {
				return false;
			}
		}
		for(int i = 1; i < c.size(); i++) {
			int orig = c.get(i - 1).s2();
			int dest = c.get(i).s1();
			Path p = Graphs.dijkstraSP(g, orig, dest, "igp");
			for(Edge e : p) {
				if(!e.isActive()) {
					return false;
				}
			}
		}
		return true;
	}

}
