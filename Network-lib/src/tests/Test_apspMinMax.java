package tests;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.junit.Test;

import IO.GraphIO;
import graph.Graph;
import graph.Graphs;

public class Test_apspMinMax {
	
	@Test
	public void test1() {
		Graph g = GraphIO.read("./data/testData/topologies/apspminmax1.ntfl");
		
		double[] igp = g.getWeigthFunction("igp").toArray();
		double[] lat = g.getWeigthFunction("lat").toArray();
		
		double[][] dist = Graphs.apspMinMax(g, igp, lat).second();
		assertTrue(TestTools.equal(dist, readAns("./data/testData/apspminmax1.ans")));
	}
	
	@Test
	public void test2() {
		Graph g = GraphIO.read("./data/testData/topologies/apspminmax2.ntfl");
		
		double[] igp = g.getWeigthFunction("igp").toArray();
		double[] lat = g.getWeigthFunction("lat").toArray();
		
		double[][] dist = Graphs.apspMinMax(g, igp, lat).second();
		assertTrue(TestTools.equal(dist, readAns("./data/testData/apspminmax2.ans")));
	}

	public double[][] readAns(String fn) {
		try {
			Scanner reader = new Scanner(new FileReader(fn));
			int n = reader.nextInt();
			double[][] ans = new double[n][n];
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					ans[i][j] = reader.nextInt();
				}
			}
			reader.close();
			return ans;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}