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
import experiments.AllPairsECMP.MinLatSRPathThread;
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

public class MinLatSRPath {

	static String experimentName = "minLat";

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
			if(!groupname.equals("ovh")) continue;
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			System.out.println(topologies.size());
			for(File f : topologies) {
				// initialize global graph data
				Graph g = GraphIO.read(f);
				System.out.println(++k + " / " + topologies.size() + ", " + g.getName());
				if(!g.getName().equals("1755")) {
					//continue;
				}
				Pair<double[][], double[][]> igpLat = Graphs.apspMinMax(g, g.getWeigthFunction("igp"), g.getWeigthFunction("lat"));
				double[][] maxLat = igpLat.second();
				
				ForwGraphs forw = new ForwGraphs(g);
				
				Segmenter segmenter = new Segmenter(g);
				// solve all pairs
				ArrayList<MinLatSRPathThread> threads = new ArrayList<>();
				System.out.println("creating threads");
				
				
				for(int orig = 0; orig < g.V(); orig++) {
					for(int dest = 0; dest < g.V(); dest++) {
						if(dest == orig) continue;
						MinLatSRPathThread thread = new MinLatSRPathThread(g, forw, forw.getForwLat(), segmenter, orig, dest);
						threads.add(thread);
					}
				}
				
				
				/*
				int ss = 53;
				int tt = 79;
				for(int orig = ss; orig <= ss; orig++) {
					for(int dest = tt; dest <= tt; dest++) {
						if(dest == orig) continue;
						//MinLatSRPathThread thread = new MinLatSRPathThread(g, forw, maxLat, segmenter, orig, dest);
						MinLatSRPathThread thread = new MinLatSRPathThread(g, forw, forw.getForwLat(), segmenter, orig, dest);
						threads.add(thread);
					}
				}
				 */
				
				
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
		//private double[][] maxLat;
		private int orig, dest;
		private Segmenter segmenter;
		private JSONObject result;
		private ForwGraphs forw;

		public MinLatSRPathThread(Graph g, ForwGraphs forw, double[][] maxLat, Segmenter segmenter, int orig, int dest) {
			this.g = g;
			this.forw = forw;
			//this.maxLat = maxLat;
			this.orig = orig;
			this.dest = dest;
			this.segmenter = segmenter;
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

			long startTime, endTime;

			startTime = System.nanoTime();
			Path minLatPath = Graphs.dijkstraSP(g, g.getWeigthFunction("lat"), orig, dest);
			endTime = System.nanoTime();
			result.put("minLatRT", (endTime - startTime) / 1e9);
			if(minLatPath == null) {
				result.put("pathExists", false);
			} else {
				result.put("pathExists", true);
				double minLat = minLatPath.getCost();
				
				
				result.put("minLatPath", minLatPath.toString());
				result.put("minLatPathIndexes", minLatPath.toIndexString());
				result.put("minLat", String.format("%.5f", minLat));

				SrPath srPath = segmenter.segment(minLatPath);
				result.put("minSegSrPath", srPath.toString());
				result.put("minSegSrPathIndexes", srPath.toIndexString());
				
				int segCost = srPath.getSegmentCost();
				result.put("minLatSeg", segCost);

				//double nomLat = maxLat[dest][orig];
				double nomLat = forw.getForwLat(orig, dest);
				result.put("nomLat", String.format("%.5f", nomLat));

				startTime = System.nanoTime();
				SrPath sr5LatPath = SrOptim.minWeightSrPath(g, orig, dest, forw.getForwLat(), g.getWeigthFunction("lat"), 5, 2);
				double sr5Lat = sr5LatPath.getWeight();
				endTime = System.nanoTime();
				
				result.put("sr5LatRT", (endTime - startTime) / 1e9);
				result.put("sr5Path", sr5LatPath.toString());
				result.put("sr5PathIndexes", sr5LatPath.toIndexString());
				result.put("sr5Lat", String.format("%.5f", sr5Lat));

				// check for errors
				JSONArray errors = new JSONArray();	
				if(!Cmp.leq(minLat, sr5Lat)) {
					errors.put("minLat > sr5Lat");
				}
				if(!Cmp.leq(sr5Lat, nomLat)) {
					errors.put("sr5Lat > nomLat");
				}			
				if(segCost > 5) {
					if(Cmp.eq(minLat, sr5Lat)) {
						Graph spDag = Graphs.shortestPathDag(g, orig, dest, "lat");
						BigInteger nbp = Dags.pathCount(spDag, orig, dest);
						if(nbp.compareTo(BigInteger.ONE) == 0) {
							errors.put("segCost > 5 and minLat = sr5Lat but there is only one min lat path");
						}
					}
				}

				if(minLat == Double.POSITIVE_INFINITY) {
					errors.put("infinite minLat but paths exist");
				}
				if(nomLat == Double.POSITIVE_INFINITY) {
					errors.put("infinite nomLat but paths exists");
				}
				if(sr5Lat == Double.POSITIVE_INFINITY) {
					errors.put("infinite sr5Lat but paths exists");
				}
				
				result.put("errors", errors);
				
				if(errors.length() > 0) {
					System.out.println(errors);
					System.exit(0);
				}				
				 
			}
		}
	}
	

}
