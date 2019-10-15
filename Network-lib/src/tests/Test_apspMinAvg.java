package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import IO.GraphIO;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import utils.ArrayExt;

public class Test_apspMinAvg {

	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/apspMinAvg1.ntfl");
		Pair<double[][], double[][]> ans = Graphs.apspMinAvg(g, g.getWeigthFunction("igp").toArray(), g.getWeigthFunction("lat").toArray());
		System.out.println(ArrayExt.toString(ans.second()));
	}
	
}
