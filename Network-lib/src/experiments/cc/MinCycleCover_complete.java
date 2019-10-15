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
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import monitoring.CoverChecker;
import monitoring.CycleCoverCG;
import monitoring.CycleCoverCGSolution;
import monitoring.MinSegCycleCover;
import utils.MyAssert;

public class MinCycleCover_complete {
	
	//static String experimentName = "minCover";
	static String experimentName = "minCover_complete";
	

	public static void main(String[] args) throws InterruptedException {
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			int k = 0;
			String groupname = group.toString().split("/")[3];
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				MyAssert.assertTrue(f.getAbsolutePath().endsWith(".json"));
				
				
				Graph g = GraphIO.read(f);
				//g.setWeightFunction("igp", g.getWeigthFunction("igp_complete"));
				
				MyAssert.assertTrue(Graphs.isComplete(g, g.getWeigthFunction("igp").toArray()));
				
				System.out.println(g.getName());
				//if(g.V() > 10) continue;
				
				File resfile = new File("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".json");
				if(resfile.exists()) {
					System.out.println("skip: " + g.getName());
					continue;
				}
				
				MinSegCycleCover minSegCover = new MinSegCycleCover(g, true);
				Pair<Boolean, String> ok = CoverChecker.checkCycleCover(minSegCover);
				MyAssert.assertTrue(ok.first());
				long t0 = System.nanoTime();
				CycleCoverCG cccg = new CycleCoverCG(g, minSegCover.getCycles(), minSegCover.getSource(), minSegCover.getMaxSeg(), Integer.MAX_VALUE);
				long t1= System.nanoTime();
				CycleCoverCGSolution cover = cccg.run(true);
				//ok = CoverChecker.checkCycleCover(minSegCover);
				ok = CoverChecker.checkCycleCover(cover.getCycles(), g, cover.getSource(), minSegCover.getMaxSeg());
				MyAssert.assertTrue(ok.first(), ok.second());
				
				JSONObject data = new JSONObject();
				data.put("initialCoverSize", minSegCover.nbCycles());
				data.put("initialCoverSeg", minSegCover.getMaxSeg());
				data.put("lpBound", cccg.getLog().lpBound());			
				data.put("values", new JSONArray(cccg.getLog().valuesToArray()));
				data.put("times", new JSONArray(cccg.getLog().timesToArray()));
				data.put("runtime", t1 - t0);
				data.put("solution", cover.toJSON());
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
