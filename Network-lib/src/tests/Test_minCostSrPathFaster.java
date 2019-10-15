package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import IO.GraphIO;
import graph.Graph;
import graph.Graphs;
import sr.ForwGraphs;
import sr.SrOptim;

public class Test_minCostSrPathFaster {
	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/topologies/rf/1239.ntfl");
		ForwGraphs forw = new ForwGraphs(g);
		long start, end;
		long total1 = 0;
		long total2 = 0;
		for(int orig = 0; orig < 1; orig++) {
			for(int dest = 0; dest < g.V(); dest++) {
				System.out.println(orig + ", " + dest);
				
				start = System.nanoTime();
				SrOptim.minWeightSrPath(g, orig, dest, forw.getForwLat(), g.getWeigthFunction("lat"), 5, 2);
				end = System.nanoTime();
				total1 += end - start;
				
				
				start = System.nanoTime();
				SrOptim.minWeightPathFaster(g, orig, dest, forw.getForwLat(), g.getWeigthFunction("lat"), 5, 2);
				end = System.nanoTime();
				total2 += end - start;
			}
		}
		System.out.println(total1);
		System.out.println(total2);
	}


}
