package experiments.sropt;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Pair;
import dataStructures.SrMetric;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import sr.SrOptim;
import utils.ArrayExt;
import utils.MyAssert;

public class SPFA extends Experiment {

	public static void main(String[] args) {
			Experiment exp = new SPFA("spfa", false, true, 5);
			ExperimentRunner.run(exp, "rf");
		}
	
		private int k;
		
		public SPFA(String name, boolean skip, boolean write, int k) {
			super(name + "_" + k, skip, write);
			this.k = k;
		}

		public JSONObject run(Graph g) {
			SrMetric lat = SrOptim.latMetric(g);
			JSONArray times = new JSONArray();
			for(int s = 0; s < g.V(); s++) {
				JSONObject timeS = new JSONObject();
				timeS.put("source", g.getNodeLabel(s));
				long start = System.nanoTime();
				double[][] ans1 = SrOptim.minWeightSrPathMatrix(g, s, lat, k, 2);
				timeS.put("normal", System.nanoTime() - start);
				start = System.nanoTime();
				double[][] ans2 = SrOptim.minWeightSrPathFaster2(g, s, lat, k, 2);
				timeS.put("faster", System.nanoTime() - start);
				if(!ArrayExt.eq(ans1, ans2)) {
					MyAssert.assertTrue(false);
				}
				times.put(timeS);
			}
			JSONObject result = new JSONObject();
			result.put("sourceRes", times);
			return result;
		}

}
