package experiments.dp;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import disjointPaths.MaxSrEDPFortzModel;
import disjointPaths.MinLatSrEDPFortzModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.MaximumEDP;
import sr.SrPath;
import utils.MyAssert;

public class MinLatEDPFortzModelEXP extends Experiment {
	
	private int maxSeg, nbRuns;
	
	public MinLatEDPFortzModelEXP(String name, boolean skip, boolean write, int maxSeg, int nbRuns) {
		super(name, skip, write);
		this.nbRuns = nbRuns;
	}

	public static void main(String[] args) {
		Experiment exp = new MinLatEDPFortzModelEXP("minLatEDPFortz", true, true, 5, 100);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		Random random = new Random(31);
		JSONObject results = new JSONObject();
		results.put("nbRuns", nbRuns);
		results.put("maxSeg", maxSeg);
		JSONArray pairRes = new JSONArray();
		for(int i = 0; i < nbRuns; i++) {
			System.out.println("----------------------------------------------------------------------------------> run: " + (i + 1) + " " + g.getName());
			JSONObject res = new JSONObject();
			int s = random.nextInt(g.V());
			int t = random.nextInt(g.V());
			while(s == t) {
				t = random.nextInt(g.V());
			}
			res.put("orig", g.getNodeLabel(s));
			res.put("dest", g.getNodeLabel(t));
			long start = System.nanoTime();
			MinLatSrEDPFortzModel m = new MinLatSrEDPFortzModel(g, s, t, 5, true);
			int nbp = (int)m.optimize();
			ArrayList<SrPath> paths = m.buildPaths();
			System.out.println(paths);
			long end = System.nanoTime();
			res.put("nbpSR", nbp);
			res.put("runtime", end - start);
		}
		results.put("pairResults", pairRes);
		return results;
	}

}
