package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import monitoring.CycleCovers;
import sr.SrPath;
import sr.SrReach;

public class CycleCoverBound {

	static String experimentName = "coverBound";

	public static void main(String[] args) throws InterruptedException {
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			String groupname = group.toString().split("/")[3];
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				// initialize global graph data
				Graph g = GraphIO.read(f);
				if(!groupname.equals("rf") || !g.getName().equals("1755")) {
					continue;
				}
				g = Graphs.largestCC(g, true);
				
				Edge[] edges = g.getEdgesByIndex();
				SrReach reach = new SrReach(g);
				
				
				//System.out.println(reach.existsCycleCover(31, 13));
				
				int s = 31;
				int k = 13;
				
				for(Edge e : edges) {
					SrPath c = CycleCovers.buildCycle(reach, e, s, k);
					System.out.println(c);
				}
			
				
				/*
				System.out.println("source: " + source + " u1: " + e.orig() + " u2: " + e.dest());
				
				reach.buildPathToNode(1, 4, 3);
				*/
				
				//Pair<Integer, Integer> sk = reach.minKEdgeCover();
				//System.out.println(g.getName() + " " + sk);
				/*
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
				*/
			}
		}

		System.out.println("done");
	}




	
}
