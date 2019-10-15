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
import experiments.MinLatSRPath.MinLatSRPathThread;
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

public class AllPairsECMP {


	static String experimentName = "ecmp";

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
			System.out.println(topologies.size());
			for(File f : topologies) {
				// initialize global graph data
				Graph g = GraphIO.read(f);
				System.out.println(++k + " / " + topologies.size() + ", " + g.getName());
				ArrayList<MinLatSRPathThread> threads = new ArrayList<>();
				System.out.println("creating threads");
				for(int orig = 0; orig < g.V(); orig++) {
					for(int dest = 0; dest < g.V(); dest++) {
						if(dest == orig) continue;
						MinLatSRPathThread thread = new MinLatSRPathThread(g, orig, dest);
						threads.add(thread);
					}
				}
				System.out.println("starting threads");
				// initialize thread solver
				ThreadSolver tsolver = new ThreadSolver(nbCores, threads);
				tsolver.run(g.getName() + "[" + nbCores + "-cores]");
				// group the results
				JSONArray results = new JSONArray();
				for(int i = 0; i < threads.size(); i++) {
					results.put(threads.get(i).getResult());
				}
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
					writer.write(results.toString(4));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("done");
	}


	public static class MinLatSRPathThread extends Thread {

		private Graph g;
		private int orig, dest;
		private JSONObject result;

		public MinLatSRPathThread(Graph g, int orig, int dest) {
			this.g = g;
			this.orig = orig;
			this.dest = dest;
		}

		public JSONObject getResult() {
			return result;
		}

		public void run() {
			result = new JSONObject();
			result.put("orig", orig + "");
			result.put("dest", dest + "");
			result.put("sourceLbl", g.getNodeLabel(orig) + "");
			result.put("destLbl", g.getNodeLabel(dest) + "");
			Graph dag = Graphs.shortestPathDag(g, orig, dest, "igp");
			BigInteger pathCount = Dags.pathCount(dag, orig, dest);
			result.put("pathCount", pathCount.toString());
			if(pathCount.compareTo(BigInteger.ZERO) > 0) {
				double maxLat = Dags.longestPath(dag, orig, dest, "lat");
				double minLat = Dags.shortestPath(dag, orig, dest, "lat");
				double avgLat = Dags.dagAveragePathCostBig(dag, orig, dest, "lat");
				result.put("maxLat", maxLat);
				result.put("minLat", minLat);
				result.put("avgLat", avgLat);
			}
		}

	}


}
