package experiments.cc;

import org.json.JSONObject;

import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Graphs;

public class NetworkDiamDeg extends Experiment {
	
	public static void main(String[] args) {
		Experiment exp = new NetworkDiamDeg("diamDeg", false, true);
		ExperimentRunner.run(exp);
	}
	
	public NetworkDiamDeg(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public JSONObject run(Graph g) {
		int edgeDiam = Graphs.edgeDiameter(g);
		int maxOutDeg = Graphs.maxOutDeg(g);
		JSONObject res = new JSONObject();
		res.put("edgeDiam", edgeDiam);
		res.put("maxOutDeg", maxOutDeg);
		System.out.println(g.V());
		return res;
	}

}