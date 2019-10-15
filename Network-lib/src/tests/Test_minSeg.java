package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Edge;
import graph.Dags;
import graph.Graph;
import graph.Graphs;
import graph.Path;
import sr.Segmenter;
import sr.SrPath;
import utils.Cmp;

public class Test_minSeg {
	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/minSeg1.ntfl");
		Path p = new Path();
		p.add(Graphs.getEdge(g, "a", "b"));
		p.add(Graphs.getEdge(g, "b", "c"));
		
		for(Edge e : p) {
			System.out.println(e);
		}
		
		p.setGraph(g);
		Segmenter seg = new Segmenter(g);
		SrPath ps = seg.segment(p);
		
		System.out.println(ps);
		
		assertTrue(ps.size() == 2);
		assertTrue(ps.get(0).isAdj());
		assertTrue(ps.get(1).isAdj());
		assertTrue(ps.get(0).getIndex() == 0);
		assertTrue(ps.get(1).getIndex() == 1);
	}
	
	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/minSeg2.ntfl");
		Path p = new Path();
		p.add(new Edge(g.getNodeIndex("a"), g.getNodeIndex("b"), 1));
		
		p.setGraph(g);
		Segmenter seg = new Segmenter(g);
		SrPath srPath = seg.segment(p);
		
		assertTrue(srPath.size() == 1);
		assertTrue(srPath.get(0).isAdj());
		assertTrue(srPath.get(0).getIndex() == 1);
	}
	
	@Test
	public void test3() {
		Graph g = GraphIO.read("./data/testData/topologies/minSeg3.ntfl");
		Segmenter seg = new Segmenter(g);
		Path p = new Path();
		Edge[] edges = g.getEdgesByIndex();
		p.add(edges[1]);
		p.add(edges[3]);
		SrPath ps = seg.segment(p);
		assertTrue(ps.size() == 2);
		assertTrue(ps.get(0).getEdge().getIndex() == 1);
		assertTrue(ps.get(1).getEdge().getIndex() == 3);
		
		p = new Path();
		p.add(edges[0]);
		p.add(edges[2]);
		ps = seg.segment(p);
		assertTrue(ps.size() == 2);
		assertTrue(ps.get(0).isNode() && ps.get(0).s1() == 0);
		assertTrue(ps.get(1).isNode() && ps.get(1).s1() == 2);
	}


}
