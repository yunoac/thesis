package experiments.dp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Pair;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.MaximumEDP;
import graph.MinLatEDP;
import graph.Path;
import monitoring.MinSegCycleCover;
import sr.Segmenter;
import sr.SrPath;
import utils.MyAssert;

public class EdgeDisjointPaths extends Experiment {
	
	public EdgeDisjointPaths(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new EdgeDisjointPaths("edgeDisjointPaths", true, true);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		Segmenter seg = new Segmenter(g);
		JSONArray res = new JSONArray();
		for(int s = 0; s < g.V(); s++) {
			for(int t = 0; t < g.V(); t++) {
				if(s == t) continue;
				JSONObject resST = new JSONObject();
				long start = System.nanoTime();
				Pair<ArrayList<Path>, Double> mcf = MinLatEDP.computePaths(g, s, t);
				ArrayList<Path> paths = mcf.first();
				int maxSeg = 0;
				for(Path path : paths) {
					SrPath p = seg.segment(path);
					maxSeg = Math.max(maxSeg, p.getSegmentCost());
				}
				long end = System.nanoTime();
				resST.put("numberEDP", paths.size());
				resST.put("maxSeg", maxSeg);
				resST.put("runtime", end - start);
				resST.put("latSum", mcf.second());
				res.put(resST);
			}
		}
		JSONObject result = new JSONObject();
		result.put("results", res);
		return result;
	}

}
