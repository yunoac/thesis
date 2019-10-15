package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Index;
import graph.Graph;
import graph.Graphs;
import math.Matrices;
import utils.Cmp;

public class Test_shortestPathDag {

	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/shortestPathDag1.ntfl");
		double[][] w = Graphs.floydWarshal(g, "igp");
		Graph dag = Graphs.shortestPathDag(g, g.getNodeIndex("A"), g.getNodeIndex("G"), w, g.getWeigthFunction("igp"));
		String s = dag.sortedEdgeListString();
		assertTrue(TestTools.compare(s, new File("./data/testData/shortestPathDag1.ans")));
	}
	
	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/shortestPathDag2.ntfl");
		double[][] w = Graphs.floydWarshal(g, "igp");
		Graph dag = Graphs.shortestPathDag(g, g.getNodeIndex("A"), g.getNodeIndex("G"), w, g.getWeigthFunction("igp"));
		String s = dag.sortedEdgeListString();
		assertTrue(TestTools.compare(s, new File("./data/testData/shortestPathDag2.ans")));
	}
	
	
	@Test
	public void testSubdag() {
		Graph g = GraphIO.read("./data/testData/topologies/shortestPathDag4.ntfl");
		double[][] w = Graphs.floydWarshal(g, "igp");
		for(int v = 0; v < g.V(); v++) {
			Graph dagv = Graphs.shortestPathDagSubgraph(g, v, w, g.getWeigthFunction("igp"));
			for(int u = 0; u < g.V(); u++) {
				Graph dagvu1 = Graphs.shortestPathDag(dagv, u);
				Graph dagvu2 = Graphs.shortestPathDag(g, v, u, w, g.getWeigthFunction("igp"));
				if(!dagvu1.sortedEdgeListString().equals(dagvu2.sortedEdgeListString())) {
					System.out.println(g.getNodeLabel(v) + " " + g.getNodeLabel(u));
					System.out.println(dagvu1.sortedEdgeListString());
					System.out.println(dagvu2.sortedEdgeListString());
					assertTrue(false);
				}
				
			}
		}
	}
	
}
