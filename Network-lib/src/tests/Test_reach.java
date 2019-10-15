package tests;

import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Edge;
import graph.Graph;
import monitoring.CycleCovers;
import sr.SrReach;

public class Test_reach {

	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/reach1.ntfl");
		SrReach reach = new SrReach(g);
		int a = g.getNodeIndex("A");
		assertTrue(reach.nodeReach(0, a).toString().equals("{}"));
		assertTrue(reach.nodeReach(1, a).toString().equals("{0}"));
		assertTrue(reach.nodeReach(2, a).toString().equals("{0, 1, 2}"));
		assertTrue(reach.nodeReach(3, a).toString().equals("{0, 1, 2, 3}"));
		assertTrue(reach.nodeReach(4, a).toString().equals("{0, 1, 2, 3}"));
	}

	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/grid5x5.ntfl");
		SrReach reach = new SrReach(g);
		int m = g.getNodeIndex("M");
		assertTrue(reach.nodeReach(0, m).toString().equals("{}"));
		assertTrue(reach.nodeReach(1, m).toString().equals("{12}"));
		assertTrue(reach.nodeReach(2, m).toString().equals("{2, 7, 10, 11, 12, 13, 14, 17, 22}"));
		assertTrue(reach.nodeReach(3, m).toString().equals("{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24}"));
		assertTrue(reach.nodeReach(4, m).toString().equals("{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24}"));		
	}

	@Test
	public void test3() {
		Graph g = GraphIO.read("./data/testData/topologies/reach2.ntfl");
		SrReach reach = new SrReach(g);
		int a = g.getNodeIndex("A");
		assertTrue(reach.nodeReach(0, a).toString().equals("{}"));
		assertTrue(reach.nodeReach(1, a).toString().equals("{0}"));
		assertTrue(reach.nodeReach(2, a).toString().equals("{0, 1, 2, 3}"));
		assertTrue(reach.nodeReach(3, a).toString().equals("{0, 1, 2, 3, 4}"));
	}
	
	@Test
	public void test5() {
		Graph g = GraphIO.read("./data/testData/topologies/cycle5.ntfl");
		SrReach reach = new SrReach(g);
		Edge[] edges = g.getEdgesByIndex();
		boolean ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 0);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 1);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 2);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 3);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 4);
		assertTrue(ok);	
		ok = CycleCovers.cycleExists(reach, edges[1], g.getNodeIndex("A"), 0);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[1], g.getNodeIndex("A"), 1);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[1], g.getNodeIndex("A"), 2);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[1], g.getNodeIndex("A"), 3);
		assertTrue(ok);
	}
	
	@Test
	public void test6() {
		Graph g = GraphIO.read("./data/testData/topologies/cycle6.ntfl");
		SrReach reach = new SrReach(g);
		Edge[] edges = g.getEdgesByIndex();
		boolean ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 0);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 1);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 2);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 3);
		assertTrue(!ok);
		ok = CycleCovers.cycleExists(reach, edges[2], g.getNodeIndex("A"), 4);
		assertTrue(ok);
	}
	
	/*
	
	@Test
	public void test6() {
		Graph g = GraphIO.read("./data/testData/topologies/reach4.ntfl");
		SrReach reach = new SrReach(g);
		Edge[] edges = g.getEdgesByIndex();
		Edge e = edges[1];
		boolean ok = reach.cycleExists(e, g.getNodeIndex("A"), 0);
		assertTrue(!ok);
		ok = reach.cycleExists(e, g.getNodeIndex("A"), 1);
		System.out.println(ok);
		ok = reach.cycleExists(e, g.getNodeIndex("A"), 2);
		System.out.println(ok);
		ok = reach.cycleExists(e, g.getNodeIndex("A"), 3);
		System.out.println(ok);
		
	}
	 */
	
}
