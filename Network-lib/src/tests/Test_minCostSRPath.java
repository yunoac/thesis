package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Index;
import graph.Graph;
import graph.Graphs;
import sr.SrOptim;
import utils.Cmp;

public class Test_minCostSRPath {
	
	@Test
	public void test1() {	
		Graph g = GraphIO.read("./data/testData/topologies/minCostSRPath1.ntfl");
		double cost = SrOptim.minMaxCostSrPath(g, g.getNodeIndex("A"), g.getNodeIndex("B"), "igp", "lat", 1, 2).getWeight();
		assertTrue(Cmp.eq(cost, 3));
		cost = SrOptim.minMaxCostSrPath(g, g.getNodeIndex("A"), g.getNodeIndex("B"), "igp", "lat", 2, 2).getWeight();
		assertTrue(Cmp.eq(cost, 2));
	}
	
	@Test
	public void test2() {	
		Graph g = GraphIO.read("./data/testData/topologies/minCostSRPath2.ntfl");
		double cost = SrOptim.minMaxCostSrPath(g, g.getNodeIndex("A"), g.getNodeIndex("B"), "igp", "lat", 1, 2).getWeight();
		assertTrue(Cmp.eq(cost, 3));
		cost = SrOptim.minMaxCostSrPath(g, g.getNodeIndex("A"), g.getNodeIndex("B"), "igp", "lat", 2, 2).getWeight();
		assertTrue(Cmp.eq(cost, 0));
	}


}
