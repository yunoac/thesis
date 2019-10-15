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

public class ExampleMinLat {

	static String experimentName = "minLat";

	public static void main(String[] args) throws InterruptedException {
		String group = "real";
		String topology = "real1.ntfl";
		
		File f = new File("./data/topologies/real/real1.ntfl");
		
		Graph g = GraphIO.read(f);
		
		String orig = "sl-bb21-syd";
		String dest = "sl-bb20-tok";
		
		System.out.println("orig: " + g.getNodeIndex(orig));
		System.out.println("dest: " + g.getNodeIndex(dest));
		
		Graph dag = Graphs.shortestPathDag(g, orig, dest, "igp");
		
		double nomLat = Dags.longestPath(dag, orig, dest, "lat");
		System.out.println(nomLat);
		
		Path p = Graphs.dijkstraSP(g, orig, dest, "lat");
		System.out.println(p.getCost());
		
		Segmenter segmenter = new Segmenter(g);
		
		SrPath srp = segmenter.segment(p);
		
		System.out.println(dag.toString());
		System.out.println(srp.toIndexString());
		
		ForwGraphs forw = new ForwGraphs(g);
		
		
		System.out.println(Graphs.shortestPathDag(g, 142, 148, "igp"));
		System.out.println("---");
		System.out.println(Graphs.shortestPathDag(g, 148, 23, "igp"));
		
		Edge e = Graphs.getEdge(g, 142, 36);
		System.out.println(g.getWeight("igp", e));
		System.out.println(g.getWeight("lat", e));
		
		e = Graphs.getEdge(g, 36, 23);
		System.out.println(g.getWeight("igp", e));
		System.out.println(g.getWeight("lat", e));
		
		
		e = Graphs.getEdge(g, 142, 148);
		System.out.println(g.getWeight("igp", e));
		System.out.println(g.getWeight("lat", e));
		
		e = Graphs.getEdge(g, 148, 45);
		System.out.println(g.getWeight("igp", e));
		System.out.println(g.getWeight("lat", e));
		
		e = Graphs.getEdge(g, 45, 23);
		System.out.println(g.getWeight("igp", e));
		System.out.println(g.getWeight("lat", e));
		
		
		System.out.println(g.outDeg(142));
		System.out.println(g.outDeg(36));
		System.out.println(g.outDeg(23));
		System.out.println(g.outDeg(148));
		System.out.println(g.outDeg(45));
		
		
	}

}
