package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Edge;
import graph.Dags;
import graph.Graph;
import graph.Graphs;
import graph.WeightFuntions;
import utils.ThreadSolver;

public class IGPAnalysis {
	
	static String experimentName = "nonSP";

	public static void main(String[] args) throws InterruptedException {
		int nbCores;
		if(args.length == 0) {
			nbCores = 4;
		} else {
			nbCores = Integer.parseInt(args[0]);			
		}
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			int k = 0;
			String groupname = group.toString().split("/")[3];
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				// initialize global graph data
				Graph g = GraphIO.read(f);
				
				ArrayList<Edge> nonSP = WeightFuntions.nonShortestPathEdges(g, "igp");
				System.out.println(g.getName() + " " + nonSP.size());
				JSONObject data = new JSONObject();
				data.put("nonSP", nonSP.size());
				data.put("V", g.V());
				data.put("E", g.E());
				JSONArray array = new JSONArray();
				BitSet b = new BitSet();
				for(Edge e : nonSP) {
					b.set(e.getIndex());
				}
				for(int v = 0; v < g.V(); v++) for(Edge e : g.outEdges(v)) {
					JSONObject edata = new JSONObject();
					edata.put("orig", e.orig());
					edata.put("dest", e.dest());
					edata.put("igp", g.getWeight("igp", e));
					edata.put("nonSP", b.get(e.getIndex()));
					array.put(edata);
				}
				
				data.put("edges", array);
				
			
				// write the results
				PrintWriter writer;
				File d;
				d = new File("./data");
				if(!d.exists()) d.mkdir();
				d = new File("./data/results");
				if(!d.exists()) d.mkdir();
				d = new File("./data/results/" + groupname);
				if(!d.exists()) d.mkdir();
				d = new File("./data/results/" + groupname + "/" + experimentName);
				if(!d.exists()) d.mkdir();

				try {
					writer = new PrintWriter(new FileWriter("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".res"));
					writer.write(data.toString(4));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("done");
	}




}
