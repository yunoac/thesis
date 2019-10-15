package experiments.sropt;

import org.json.JSONObject;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import sr.ForwGraphs;

public class ComputeRatios extends Experiment {

	public static void main(String[] args) {
			Experiment exp = new ComputeRatios("ratios", false, true);
			ExperimentRunner.run(exp, "rf");
		}
		public ComputeRatios(String name, boolean skip, boolean write) {
			super(name, skip, write);
		}

		public JSONObject run(Graph g) {
			long start = System.nanoTime();
			//SrRatios ratios = new SrRatios(g);
			ForwGraphs fw = new ForwGraphs(g);
			long end = System.nanoTime();
			JSONObject result = new JSONObject();
			result.put("rutime", end - start);
			return result;
		}

}
