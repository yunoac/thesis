package experiments.cc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import monitoring.CoverChecker;
import monitoring.CycleCovers;
import monitoring.MinSegCycleCover;
import sr.Segment;
import sr.SrPath;
import sr.SrReach;
import utils.MyAssert;

public class MinSegCover {
	
	public static String experimentName = "minSegCover";

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
				
				//if(!(groupname.equals("zoo") && g.getName().equals("Interoute"))) continue;
				
				File resFile = new File("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".res");
				if(resFile.exists()) {
					System.out.println("skip: " + g.getName());
					continue;
				}
				
				
				MyAssert.assertTrue(Graphs.nbSCC(g) == 1);
				
				JSONObject data = new JSONObject();
				System.out.println("solving: " + g.getName() + " V=" + g.V() + " E=" + g.E());
				long start = System.nanoTime();
				MinSegCycleCover cover = new MinSegCycleCover(g, true);
				long end = System.nanoTime();
				Pair<Boolean, String> status = CoverChecker.checkCycleCover(cover.getCycles(), g, cover.getSource(), cover.getMaxSeg());
				
				if(!status.first()) {
					System.out.println(status);
					System.out.println("problem for: " + g.getName());
					System.exit(0);
				}
				
				data.put("runtime", end - start);
				data.put("segmentCost", cover.getMaxSeg());
				data.put("source", g.getNodeLabel(cover.getSource()));
				data.put("nbCycles", cover.getCycles().size());
				
				JSONArray cycles = new JSONArray();
				for(SrPath c : cover.getCycles()) {
					String s = "";
					for(int i = 0; i < c.size(); i++) {
						Segment seg = c.get(i);
						s += g.getNodeLabel(seg.s1());
						if(seg.isAdj()) {
							s += "," + g.getNodeLabel(seg.s2());
						}
						if(i < c.size() - 1) {
							s += " ";
						}
					}
					cycles.put(s);
				}
				
				data.put("cycles", cycles);
				
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
					writer = new PrintWriter(new FileWriter("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".json"));
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
