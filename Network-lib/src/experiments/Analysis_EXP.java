package experiments;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.STIO;
import dataStructures.Edge;
import dataStructures.Pair;
import dataStructures.RDPDemand;
import disjointPaths.GenerateST;
import disjointPaths.MaxSrEDPFortzModel;
import disjointPaths.MinLatSrEDPFortzModel;
import disjointPaths.SR2DisjointPaths;
import disjointPaths.SR2EDPModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Dags;
import graph.Graph;
import graph.Graphs;
import graph.MaximumEDP;
import graph.Path;
import sr.ForwGraphs;
import sr.SrPath;
import utils.Cmp;
import utils.MyAssert;

public class Analysis_EXP extends Experiment {

	public Analysis_EXP(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new Analysis_EXP("Analysis", false, true);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		JSONObject results = new JSONObject();
		// topology size
		results.put("V", g.V());
		results.put("E", g.E());
		results.put("G", g.V() + g.E());
		// non-shortest path edges
		ForwGraphs forw = new ForwGraphs(g);
		double nonSP = 0;
		for(Edge e : g.getEdgesByIndex()) {
			if(!forw.belongsToIGPShortestPath(e.orig(), e.dest(), e)) {
				nonSP += 1;
			}
		}
		results.put("nonSP", nonSP / g.E());
		// ECMP
		double ecmp = 0;
		double[] igp = g.getWeigthFunction("igp").toArray();
		JSONArray pathCount = new JSONArray();
		for(int s = 0; s < g.V(); s++) {
			for(int t = 0; t < g.V(); t++) {
				if(s == t) continue;
				boolean hasECMP = Graphs.hasECMP(g, s, t, igp);
				if(hasECMP) {
					ecmp += 1;					
				}
				BigInteger pc = Dags.pathCount(forw.getDag(s), s, t);
				MyAssert.assertTrue((hasECMP == false && pc.equals(BigInteger.ONE) || (hasECMP && pc.compareTo(BigInteger.ONE) > 0)));
				pathCount.put(pc);
			}
		}
		System.out.println(ecmp / (g.V() * g.V() - g.V()));
		results.put("ECMP", ecmp / (g.V() * g.V() - g.V()));
		results.put("spCount", pathCount);
		// min cut
		JSONArray minCut = new JSONArray();
		JSONArray delta = new JSONArray();
		for(int s = 0; s < g.V(); s++) {
			for(int t = 0; t < g.V(); t++) {
				if(s == t) continue;
				ArrayList<Path> maxEDP = MaximumEDP.computePaths(g, s, t);
				minCut.put(maxEDP.size());
				delta.put(maxEDP.size() - Math.min(g.outDeg(s), g.outDeg(t)));
			}
		}
		results.put("minCut", minCut);
		results.put("delta", delta);
		// degrees
		JSONArray degree = new JSONArray();
		for(int v = 0; v < g.V(); v++) {
			degree.put(g.outDeg(v));
		}
		results.put("degrees", degree);
		return results;
	}

}
