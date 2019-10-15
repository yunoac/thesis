package tests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Pair;
import dataStructures.Triple;
import graph.Graph;
import sr.ForwGraphs;
import sr.SrOptim;
import sr.SrPath;
import utils.ArrayExt;
import utils.Cmp;

public class Test_MaxCapSRPath {
	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/maxCapSRPath.ntfl");
		ForwGraphs fw = new ForwGraphs(g);
		SrPath p = SrOptim.maxCapSrPath(g, fw, g.getNodeIndex("A"), g.getNodeIndex("C"), 10);
		System.out.println(p.getWeight());
		assertTrue(p.getWeight() == 1);
	}	
	
	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/maxCapSRPath2.ntfl");
		ForwGraphs fw = new ForwGraphs(g);
		SrPath p = SrOptim.maxCapSrPath(g, fw, g.getNodeIndex("A"), g.getNodeIndex("C"), 10);
		System.out.println(p.getWeight());
		assertTrue(p.getWeight() == 1);
	}	

	@Test
	public void test3() {
		Graph g = GraphIO.read("./data/testData/topologies/maxCapSRPath2.ntfl");
		ForwGraphs fw = new ForwGraphs(g);
		SrPath p = SrOptim.maxCapSrPath(g, fw, g.getNodeIndex("A"), g.getNodeIndex("D"), 10);
		System.out.println(p.getWeight());
		assertTrue(p.getWeight() == 0);
	}	
	
}
