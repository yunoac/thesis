package experiments;

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
import monitoring.CoverChecker;
import monitoring.CycleCoverCG;
import monitoring.CycleCoverCGSolution;
import monitoring.CycleCoverRounder;
import monitoring.MinSegCycleCover;
import sr.SrPath;
import utils.MyAssert;

public class MinCycleCover {
	
	static String experimentName = "minCover";
	
	static void test() {
		
		Graph g = GraphIO.read("./data/topologies/rf/1755.json");
		MinSegCycleCover minSegCover = new MinSegCycleCover(g, true);
		CycleCoverCG cccg = new CycleCoverCG(g, minSegCover.getCycles(), minSegCover.getSource(), minSegCover.getMaxSeg(), Integer.MAX_VALUE);
		CycleCoverCGSolution cover = cccg.run(true);
		Pair<Boolean, String> ok = CoverChecker.checkCycleCover(minSegCover);
		MyAssert.assertTrue(ok);
		ArrayList<SrPath> lpCycles = cover.getCycles();
		System.out.println("greedy");
		ArrayList<SrPath> greedyCycles = CycleCoverRounder.greedyRounder(g, lpCycles);
		System.out.println(minSegCover.nbCycles());
		System.out.println(cover.LPbound());
		System.out.println(greedyCycles.size());
		System.exit(0);

	}

	public static void main(String[] args) throws InterruptedException {
		
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			int k = 0;
			String groupname = group.toString().split("/")[3];
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				Graph g = GraphIO.read(f);
				if(g.V() > 50) continue;
				
				MinSegCycleCover minSegCover = new MinSegCycleCover(g, true);
				Pair<Boolean, String> ok = CoverChecker.checkCycleCover(minSegCover);
				MyAssert.assertTrue(ok.first());
				long t0 = System.nanoTime();
				CycleCoverCG cccg = new CycleCoverCG(g, minSegCover.getCycles(), minSegCover.getSource(), minSegCover.getMaxSeg(), Integer.MAX_VALUE);
				long t1= System.nanoTime();
				CycleCoverCGSolution cover = cccg.run(true);
				ok = CoverChecker.checkCycleCover(minSegCover);
				MyAssert.assertTrue(ok.first());
				
				JSONObject data = new JSONObject();
				data.put("initialCoverSize", minSegCover.nbCycles());
				data.put("initialCoverSeg", minSegCover.getMaxSeg());
				data.put("lpBound", cccg.getLog().lpBound());			
				data.put("values", new JSONArray(cccg.getLog().valuesToArray()));
				data.put("times", new JSONArray(cccg.getLog().timesToArray()));
				data.put("finalCoverSize", cover.nbCycles());
				data.put("runtime", t1 - t0);
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
