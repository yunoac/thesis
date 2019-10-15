package tests;

import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import sr.SrReach;

public class Test_minReach {

	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/reach1.ntfl");
		SrReach reach = new SrReach(g);
		assertTrue(reach.getMinSegNodeCover() == 3);
		assertTrue(reach.getMinSegEdgeCover() == 3);
	}

	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/grid5x5.ntfl");
		SrReach reach = new SrReach(g);
		System.out.println(reach);
		assertTrue(reach.getMinSegNodeCover() == 3);
		assertTrue(reach.getMinSegEdgeCover() == 4);
	}

	@Test
	public void test3() {
		Graph g = GraphIO.read("./data/testData/topologies/reach2.ntfl");
		SrReach reach = new SrReach(g);
		assertTrue(reach.getMinSegNodeCover() == 3);
		assertTrue(reach.getMinSegEdgeCover() == 4);
	}
	
	@Test
	public void test4() {
		Graph g = GraphIO.read("./data/testData/topologies/reach3.ntfl");
		SrReach reach = new SrReach(g);
		assertTrue(reach.getMinSegNodeCover() == 2);
		assertTrue(reach.getMinSegEdgeCover() == 2);
	}
	
}
