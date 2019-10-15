package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Dags;
import graph.Graph;
import graph.Path;

public class Test_DagPathDec {
	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/topologies/other/grid2x2.ntfl");
		ArrayList<Pair<Path, Double>> paths = Dags.pathDecomposition(g, g.getNodeIndex("A"), g.getNodeIndex("D"), "lat");
		assertTrue(paths.size() == 2);
		double[] cover = new double[g.E()];
		for(Pair<Path, Double> p : paths) {
			for(Edge e : p.first()) {
				cover[e.getIndex()] += p.second();
			}
		}
		assertTrue(Arrays.equals(cover, g.getWeigthFunction("lat").toArray()));
	}
	
	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/topologies/other/grid2x2_2.ntfl");
		ArrayList<Pair<Path, Double>> paths = Dags.pathDecomposition(g, g.getNodeIndex("A"), g.getNodeIndex("D"), "lat");
		assertTrue(paths.size() == 2);
		double[] cover = new double[g.E()];
		for(Pair<Path, Double> p : paths) {
			for(Edge e : p.first()) {
				cover[e.getIndex()] += p.second();
			}
		}
		assertTrue(Arrays.equals(cover, g.getWeigthFunction("lat").toArray()));
	}


	
	@Test
	public void test3() {
		Graph g = GraphIO.read("./data/topologies/other/dag1.ntfl");
		ArrayList<Pair<Path, Double>> paths = Dags.pathDecomposition(g, g.getNodeIndex("A"), g.getNodeIndex("D"), "lat");
		assertTrue(paths.size() == 3);
		double[] cover = new double[g.E()];
		for(Pair<Path, Double> p : paths) {
			for(Edge e : p.first()) {
				cover[e.getIndex()] += p.second();
			}
		}
		assertTrue(Arrays.equals(cover, g.getWeigthFunction("lat").toArray()));
	}

}
