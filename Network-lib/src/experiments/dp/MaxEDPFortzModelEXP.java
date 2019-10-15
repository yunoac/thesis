package experiments.dp;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import disjointPaths.MaxSrEDPFortzModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.MaximumEDP;
import utils.MyAssert;

public class MaxEDPFortzModelEXP extends Experiment {
	
	private int maxSeg, nbRuns;
	
	public MaxEDPFortzModelEXP(String name, boolean skip, boolean write, int maxSeg, int nbRuns) {
		super(name, skip, write);
		this.nbRuns = nbRuns;
	}

	public static void main(String[] args) {
		Experiment exp = new MaxEDPFortzModelEXP("maxEDPMip", true, true, 5, 100);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		Random random = new Random(31);
		JSONObject results = new JSONObject();
		results.put("nbRuns", nbRuns);
		results.put("maxSeg", maxSeg);
		JSONArray pairRes = new JSONArray();
		for(int i = 0; i < nbRuns; i++) {
			System.out.println("----------------------------------------------------------------------------------> run: " + (i + 1));
			JSONObject res = new JSONObject();
			int s = random.nextInt(g.V());
			int t = random.nextInt(g.V());
			while(s == t) {
				t = random.nextInt(g.V());
			}
			res.put("orig", g.getNodeLabel(s));
			res.put("dest", g.getNodeLabel(t));
			long start = System.nanoTime();
			MaxSrEDPFortzModel m = new MaxSrEDPFortzModel(g, s ,t, 5, true);
			int nbp = (int)m.optimize();
			m.buildPaths();
			
			long end = System.nanoTime();
			res.put("nbpSR", nbp);
			res.put("runtime", end - start);
			int nbpFlow = MaximumEDP.computePaths(g, s, t).size();
			res.put("nbpFlow", nbpFlow);
			pairRes.put(res);
			MyAssert.assertTrue(nbp <= nbpFlow);
		}
		results.put("pairResults", pairRes);
		return results;
	}

}
