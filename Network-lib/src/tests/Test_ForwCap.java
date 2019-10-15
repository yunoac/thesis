package tests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Pair;
import dataStructures.Triple;
import graph.Graph;
import sr.ForwGraphs;
import utils.ArrayExt;
import utils.Cmp;

public class Test_ForwCap {
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/forwCap.ntfl");
		ForwGraphs fw = new ForwGraphs(g);
		Triple<double[], double[], Double> loads = fw.getLoads(g.getNodeIndex("A"), g.getNodeIndex("D"));
		System.out.println(Arrays.toString(loads.first()));
		System.out.println(Arrays.toString(loads.second()));
		System.out.println(loads.third());
		assertTrue(ArrayExt.eq(loads.first(), new double[] {1, 0.5, 0.5, 1}));
		assertTrue(ArrayExt.eq(loads.second(), new double[] {0.5, 0.5, 0.5, 0.5}));
		assertTrue(Cmp.eq(loads.third(), 1 / 0.5));
	}	

	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/forwCap2.ntfl");
		ForwGraphs fw = new ForwGraphs(g);
		Triple<double[], double[], Double> loads = fw.getLoads(g.getNodeIndex("A"), g.getNodeIndex("K"));
		System.out.println(Arrays.toString(loads.first()));
		System.out.println(Arrays.toString(loads.second()));	
		System.out.println(loads.third());
		assertTrue(ArrayExt.eq(loads.first(), new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.75, 0.5, 0.5, 0.25, 1.0}));
		assertTrue(ArrayExt.eq(loads.second(), new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.5, 0.25, 0.25, 0.75, 0.25}));
		assertTrue(Cmp.eq(loads.third(), 1 / 0.75));
	}
	
	@Test
	public void test3() {
		Graph g = GraphIO.read("./data/testData/topologies/forwCap3.ntfl");
		ForwGraphs fw = new ForwGraphs(g);
		double[][] cap = fw.getForwCap();
		System.out.println(ArrayExt.toString(cap));
		assertTrue(ArrayExt.eq(cap[0], new double[] {Double.POSITIVE_INFINITY, 1, 0, 0}));
		assertTrue(ArrayExt.eq(cap[1], new double[] {1, Double.POSITIVE_INFINITY, 0, 0}));
		assertTrue(ArrayExt.eq(cap[2], new double[] {0, 0, Double.POSITIVE_INFINITY, 1}));
		assertTrue(ArrayExt.eq(cap[3], new double[] {0, 0, 1, Double.POSITIVE_INFINITY}));
	}
	
	
	
}
