package experiments.cc;

import static org.junit.Assert.assertTrue;

import org.json.JSONObject;

import dataStructures.Pair;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Graphs;
import graph.WeightFunction;
import monitoring.IGPFinder;

public class PrimeIGP extends Experiment {
	
	public static void main(String[] args) {
		Experiment exp = new PrimeIGP("primeIGP", false, true);
		ExperimentRunner.run(exp);
	}
	
	public PrimeIGP(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public JSONObject run(Graph g) {
		Pair<WeightFunction, Integer> p = IGPFinder.primeECMPFreeIGP(g);
		WeightFunction w = p.first();
		assertTrue(Graphs.isECMPFreeComplete(g, w.toArray()));
		JSONObject res = new JSONObject();
		res.put("igp", w.maxWeight());
		res.put("s", p.second());
		return res;
	}

}