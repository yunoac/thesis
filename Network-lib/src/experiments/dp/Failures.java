package experiments.dp;

import java.util.BitSet;

import dataStructures.Edge;
import graph.Graph;

public class Failures {
	
	public static BitSet[] singleEdge(Graph g) {
		Edge[] edges = g.getEdgesByIndex();
		BitSet[] F = new BitSet[edges.length];
		for(Edge e : edges) {
			F[e.getIndex()] = new BitSet();
			F[e.getIndex()].set(e.getIndex());
		}
		return F;
	}

}
