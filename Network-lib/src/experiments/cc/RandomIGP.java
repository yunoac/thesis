package experiments.cc;


import org.json.JSONObject;

import IO.GraphIO;
import dataStructures.Pair;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Graphs;
import graph.WeightFunction;
import monitoring.IGPFinder;
import utils.MyAssert;

public class RandomIGP extends Experiment {

	public static void main(String[] args) {
		Experiment exp = new RandomIGP("randomIGP", false, true);
		ExperimentRunner.run(exp);
	}

	public RandomIGP(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public JSONObject run(Graph g) {
		int average1 = 0;
		int average2 = 0;
		int average3 = 0;
		for(int i = 0; i < 1; i++) {
			average1 += IGPFinder.randomIGP(g, 100).second();
		}
		JSONObject res = new JSONObject();
		res.put("a100000", average1 / 100.0);
		res.put("a10000", average2 / 100.0);
		res.put("a1000", average3 / 100.0);
		return res;

	}

}