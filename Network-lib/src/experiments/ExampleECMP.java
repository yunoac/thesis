package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Dags;
import graph.Graph;
import graph.Graphs;
import graph.Path;
import sr.ForwGraphs;
import sr.SrOptim;
import sr.Segmenter;
import sr.SrPath;
import utils.Cmp;
import utils.ThreadSolver;

public class ExampleECMP {

	static String experimentName = "minLat";

	public static void main(String[] args) throws InterruptedException {
		String group = "real";
		String topology = "real1.ntfl";

		File f = new File("./data/topologies/real/real1.ntfl");

		Graph g = GraphIO.read(f);

		int orig = 84, dest = 133;
		/*
		double maxDiff = 0;
		for(int o = 0; o < g.V(); o++) {
			for(int d = 0; d < g.V(); d++) {
				System.out.println(o + " " + d);
				Graph dag = Graphs.shortestPathDag(g, o, d, "igp");
				BigInteger cnt = Dags.pathCount(dag, o, d);
				if(cnt.compareTo(BigInteger.valueOf(2)) == 0) {
					double min = Dags.shortestPath(dag, o, d, "lat");
					double max = Dags.longestPath(dag, o, d, "lat");
					if(max - min > maxDiff) {
						maxDiff = max - min;
						orig = o;
						dest = d;
					}
				}
			}
		}
		*/

		System.out.println(orig);
		System.out.println(dest);
		Graph dag = Graphs.shortestPathDag(g, orig, dest, "igp");
		BigInteger cnt = Dags.pathCount(dag, orig, dest);
		double min = Dags.shortestPath(dag, orig, dest, "lat");
		double max = Dags.longestPath(dag, orig, dest, "lat");

		System.out.println(min);
		System.out.println(max);
		

		for(int v = 0; v < dag.V(); v++) {
			for(Edge e : dag.outEdges(v)) {
				System.out.println(e.toIndexString() + " " + dag.getWeight("igp", e) + " " +dag.getWeight("lat", e));
			}
		}

	}

}
