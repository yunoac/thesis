package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import dataStructures.Index;
import graph.Graph;
import graph.Graphs;

public class Test_largestCC {
	
	@Test
	public void test1() {
		Index<String> index = new Index<>();
		index.add("A");
		index.add("B");
		index.add("X");
		index.add("Y");
		index.add("Z");
		Graph g = new Graph(index);
		g.connectBoth("A", "B");
		g.connectBoth("X", "Y");
		g.connectBoth("Y", "Z");
		g.connectBoth("X", "Z");
		
		Graph cc = Graphs.largestCC(g);
		assertTrue(cc.V() == 3);
		assertTrue(cc.getNodeLabel(0).equals("X"));
		assertTrue(cc.getNodeLabel(1).equals("Y"));
		assertTrue(cc.getNodeLabel(2).equals("Z"));
	}


}
