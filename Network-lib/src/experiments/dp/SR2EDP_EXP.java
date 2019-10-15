package experiments.dp;

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
import graph.Graph;
import graph.MaximumEDP;
import graph.Path;
import sr.ForwGraphs;
import sr.SrPath;
import utils.Cmp;
import utils.MyAssert;

public class SR2EDP_EXP extends Experiment {

	private int maxSeg, nbRuns;

	public SR2EDP_EXP(String name, boolean skip, boolean write, int maxSeg, int nbRuns) {
		super(name + "_" + maxSeg, skip, write);
		this.maxSeg = maxSeg;
		this.nbRuns = nbRuns;
	}

	public static void main(String[] args) {
		/*
		Experiment exp = new SR2EDP_EXP("SR2EDP", false, true, 3, 100);
		ExperimentRunner.run(exp, 50, Integer.MAX_VALUE);
		 */
		Experiment exp = new SR2EDP_EXP("SR2EDP", true, true, 4, 100);
		ExperimentRunner.run(exp, 50, Integer.MAX_VALUE);
		exp = new SR2EDP_EXP("SR2EDP", false, true, 5, 100);
		ExperimentRunner.run(exp, 50, Integer.MAX_VALUE);	
		
		/*
		Experiment exp = new SR2EDP_EXP("SR2EDP", false, true, 4, 100);
		ExperimentRunner.run(exp, "zoo", "Garr201104");	
		*/
	}

	public JSONObject run(Graph g) {
		JSONObject results = new JSONObject();
		results.put("nbRuns", nbRuns);
		results.put("maxSeg", maxSeg);
		JSONArray pairRes = new JSONArray();
		System.out.println("generating demands");
		ArrayList<RDPDemand> demands = GenerateST.generateSSTTBiconnected(g, 100);
		System.out.println("demands generated");
		for(int i = 0; i < demands.size(); i++) {
			JSONObject res = new JSONObject();
			RDPDemand demand = demands.get(i);
			System.out.println("----------------------------------------------------------------------------------> run: " + i + " " + maxSeg + "/" + g.getName());
			res.put("s1", g.getNodeLabel(demand.s1()));
			res.put("s2", g.getNodeLabel(demand.s2()));
			res.put("t1", g.getNodeLabel(demand.t1()));
			res.put("t2", g.getNodeLabel(demand.t2()));
			long start = System.nanoTime();
			SR2EDPModel m = new SR2EDPModel(g, demand, maxSeg, true);
			long end = System.nanoTime();
			res.put("mip_runtime", end - start);
			
			Double lat = m.optimize();
			System.out.println(demand);
			res.put("mip_pathsExist", lat != null);
			if(lat != null) {
				Pair<SrPath, SrPath> paths = m.buildPaths();
				System.out.println(paths.first());
				System.out.println(paths.second());
				MyAssert.assertTrue(!paths.first().getEdgeSet(g).intersects(paths.second().getEdgeSet(g)), "intersection in MIP solution");
				res.put("mip_lat", lat);
				res.put("mip_p1", paths.first().toFileString(g));
				res.put("mip_p2", paths.second().toFileString(g));
				double max = Math.max(paths.first().getWeight(), paths.second().getWeight());
				MyAssert.assertTrue(Cmp.eq(lat, max), lat + " " + max);
			}
			
			System.out.println("computing RDP");
			start = System.nanoTime();
			SR2DisjointPaths srdp = new SR2DisjointPaths(g);
			Pair<SrPath, SrPath> paths2 = srdp.computePaths(demand, maxSeg);
			System.out.println(paths2);
			end = System.nanoTime();
			res.put("dedicated_runtime", end - start);
			
			if(lat != null) {
				MyAssert.assertTrue(paths2 != null, "existence: " + lat + " " + paths2);				
			}
			
			res.put("dedicated_pathsExist", paths2 != null);
			if(paths2 != null) {
				
				MyAssert.assertTrue(!paths2.first().getEdgeSet(g).intersects(paths2.second().getEdgeSet(g)), "intersection dedicated solution");
				
				res.put("dedicated_p1", paths2.first().toFileString(g));
				res.put("dedicated_p2", paths2.second().toFileString(g));
				double max2 = Math.max(paths2.first().getWeight(), paths2.second().getWeight());
				res.put("dedicated_lat", max2);
				
				if(lat != null) {					
					MyAssert.assertTrue(Cmp.geq(lat, max2), "lat: " + lat + " < " + max2);
				}
			}
			
			pairRes.put(res);
		}
		results.put("pairResults", pairRes);
		return results;
	}

}
