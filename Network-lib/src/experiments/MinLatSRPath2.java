package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Pair;
import graph.Dags;
import graph.Graph;
import graph.Graphs;
import sr.SrOptim;

public class MinLatSRPath2 {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
	    JSONObject results = new JSONObject();
		ArrayList<File> instances = IOTools.listTopologies("./data/topologies");
		for(File f : instances) {
			Graph g = GraphIO.read(f);
			if(!g.getName().equals("1755")) continue;
			Pair<double[][], double[][]> igpLat = Graphs.apspMinMax(g, g.getWeigthFunction("igp"), g.getWeigthFunction("lat"));
			double[][] maxlat = igpLat.second();
			double[][] minlat = Graphs.apspMinMin(g, g.getWeigthFunction("igp"), g.getWeigthFunction("lat")).second();
			double[][] avglat = new double[g.V()][g.V()];
			for(int v = 0; v < g.V(); v++) {
				Graph dagv = Graphs.shortestPathDagSubgraph(g, v, igpLat.first(), g.getWeigthFunction("igp"));
				avglat[v] = Dags.dagAveragePathCost(dagv, v, g.getWeigthFunction("lat"));
			}
			System.out.println(g.getName());
			double total = g.V() * g.V() - g.V();
			double ratio = 0;
			int cnt = 0;
			JSONArray pairResults = new JSONArray();
			for(int v = 0; v < g.V(); v++) {
				for(int u = 0; u < g.V(); u++) {
					if(u == v) continue;
					JSONObject result = new JSONObject();
					result.put("source", v + "");
					result.put("dest", u + "");
					cnt++;
					double srMaxLat = SrOptim.minWeightSrPath(g, u, v, maxlat, g.getWeigthFunction("lat"), 5, 2).getWeight();
					result.put("srMaxLat", String.format("%.5f", srMaxLat));
					
					double srMinLat = SrOptim.minWeightSrPath(g, u, v, minlat, g.getWeigthFunction("lat"), 5, 2).getWeight();
					result.put("srMinLat", String.format("%.5f", srMinLat));
					
					double srAvgLat = SrOptim.minWeightSrPath(g, u, v, avglat, g.getWeigthFunction("lat"), 5, 2).getWeight();
					result.put("srAvgLat", String.format("%.5f", srAvgLat));
					
					double nomlat = maxlat[u][v];
					result.put("nomlat", String.format("%.5f", nomlat));
					
					double percent = 100 * cnt / total;
					System.out.println(percent);
					pairResults.put(result);
				}
			}
			JSONObject instanceResults = new JSONObject();
			instanceResults.put("instance", f);
			instanceResults.put("pairResults", pairResults);
			results.put(g.getName(), instanceResults);
			System.out.println(ratio / total);
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter("./data/results/minLatSR.json"));
			writer.write(results.toString(4));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}