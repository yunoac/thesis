package experiments.dp;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import disjointPaths.MaxSrEDPFortzModel;
import disjointPaths.MaxSrEDPSegmentModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Graphs;
import graph.MaximumEDP;
import sr.SrPath;
import utils.MyAssert;

public class MaxEDPSegmentModelEXP extends Experiment {
	
	private int maxSeg, nbRuns;
	
	public MaxEDPSegmentModelEXP(String name, boolean skip, boolean write, int maxSeg, int nbRuns) {
		super(name, skip, write);
		this.nbRuns = nbRuns;
	}

	public static void main(String[] args) {
		Experiment exp = new MaxEDPSegmentModelEXP("maxEDPSegmentModel", true, true, 5, 100);
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
			
			if(i <= 10) {
				System.out.println("skip: " + i);
				continue;
			}
			
			res.put("orig", g.getNodeLabel(s));
			res.put("dest", g.getNodeLabel(t));
			long start = System.nanoTime();
			MaxSrEDPSegmentModel m = new MaxSrEDPSegmentModel(g, s ,t, 5, true);
			
			int nbp = (int)m.optimize();
			
			MyAssert.assertTrue(Graphs.connected(g, s, t), "disconnected");
			MyAssert.assertTrue(nbp > 0, "0 paths");
			
			ArrayList<SrPath> paths = m.buildPaths();
			System.out.println("----");
			for(SrPath p : paths) {
				System.out.println(p);
			}
			System.out.println("----");
			long end = System.nanoTime();
			res.put("nbpSR", nbp);
			res.put("runtime", end - start);
			int nbpFlow = MaximumEDP.computePaths(g, s, t).size();
			res.put("nbpFlow", nbpFlow);
			pairRes.put(res);
			
			System.out.println();
			System.out.println(res);
			System.out.println();
			
			MyAssert.assertTrue(nbp <= nbpFlow, nbp + " " + nbpFlow);
		}
		results.put("pairResults", pairRes);
		return results;
	}

}
