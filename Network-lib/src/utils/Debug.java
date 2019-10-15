package utils;

import java.util.BitSet;

import IO.GraphIO;
import dataStructures.Bitset;
import dataStructures.Edge;
import graph.Graph;
import sr.ForwGraphs;
import sr.SrPath;

public class Debug {
	
	public static void main(String[] args) {
		Debug debug = new Debug("zoo", "HiberniaUs");
		debug.printDAG(17, 16);
		debug.printDAG(16, 20);
		System.out.println();
		debug.printDAG(18, 21);
		
		SrPath p = new SrPath(new int[] {17, 16, 20});
		SrPath q = new SrPath(new int[] {18, 21});
		System.out.println(debug.areDisjoint(p, q));
		System.out.println(debug.lat(p));
		System.out.println(debug.lat(q));
		
		
	}
	
	private Graph g;
	private Edge[] edges;
	private ForwGraphs forw;
	
	public Debug(Graph g) {
		this.g = g;
		init();
	}
	
	public Debug(String grouname, String graphname) {
		g = GraphIO.read(grouname, graphname + ".json");
		init();
	}
	
	private void init() {
		edges = g.getEdgesByIndex();
		forw = new ForwGraphs(g);	
	}
	
	public void printDAG(int orig, int dest) {
		for(Edge e : edges) {
			if(forw.getForwE(orig, dest).get(e.getIndex())) {
				System.out.println(e);
			}
		}
	}
	
	public boolean areDisjoint(int[] nodes1, int[] nodes2) {
		SrPath p = new SrPath(nodes1);
		SrPath q = new SrPath(nodes2);
		return areDisjoint(p, q);
	}
	
	public boolean areDisjoint(SrPath p, SrPath q) {
		BitSet Ep = p.getEdgeSet(g, forw);
		BitSet Eq = p.getEdgeSet(g, forw);
		return Ep.intersects(Eq);
	}
	
	public double lat(SrPath p) {
		return p.getLat(g, forw);
	}
	


}
