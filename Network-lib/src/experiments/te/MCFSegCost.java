package experiments.te;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.DemandIO;
import dataStructures.Pair;
import disjointPaths.MaxSrEDPFortzModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.MaximumEDP;
import graph.Path;
import sr.Segmenter;
import sr.SrPath;
import te.Demand;
import te.MCF;
import te.TEInstance;
import utils.MyAssert;

public class MCFSegCost extends Experiment {

	public MCFSegCost(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new MCFSegCost("MCFSegCost", true, true);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		ArrayList<Demand> demands = DemandIO.readDemands("./data/demands/" + getCurrentGroup() + "/" + g.getName() + ".demands");
		TEInstance instance = new TEInstance(g, demands);
		MCF mcf = new MCF(instance, false);
		mcf.optimize();
		Segmenter seg = new Segmenter(g);
		HashMap<Demand, ArrayList<Pair<Path, Double>>> paths = mcf.getPathFlows();
		JSONArray costs = new JSONArray();
		for(Demand d : demands) {
			for(Pair<Path, Double> pf : paths.get(d)) {
				Path p = pf.first();
				SrPath srp = seg.segment(p);
				int cost = srp.getSegmentCost();
				costs.put(cost);
			}
		}
		JSONObject res = new JSONObject();
		res.put("costs", costs);
		return res;
	}

}
