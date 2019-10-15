package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import IO.GraphIO;
import graph.Dags;
import graph.Graph;
import utils.Cmp;

public class Test_averageDistance {
	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/dag1.ntfl");
		
		double[] dagAveragePathCost1 = Dags.dagAveragePathCost(g, g.getNodeIndex("A"), g.getWeigthFunction("igp"));
		
		double[] dagAveragePathCost2 = Dags.dagAveragePathCostBig(g, g.getNodeIndex("A"), g.getWeigthFunction("igp"));
		
		assertTrue(Cmp.eq(dagAveragePathCost1, dagAveragePathCost2));
		assertTrue(Cmp.eq(dagAveragePathCost1, new double[] {0.0, 1.0, 1.5, 2.5}));
		
	}

}
