package experiments.cc;

import org.json.JSONArray;
import org.json.JSONObject;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Path;
import monitoring.MinSegCycleCover;
import sr.Segmenter;
import sr.SrPath;
import utils.MyAssert;

public class IdentificationCyclesCost extends Experiment {
	
	public IdentificationCyclesCost(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new IdentificationCyclesCost("identificationCost", false, true);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		// compute with regular IGP
		long start = System.nanoTime();
		MinSegCycleCover minSegCover = new MinSegCycleCover(g, true);
		long end = System.nanoTime();
		Segmenter segmenter = new Segmenter(g);
		JSONArray costs = new JSONArray();
		for(SrPath src : minSegCover.getCycles()) {
			MyAssert.assertTrue(src.isCycle());
			Path c = src.path(g);
			MyAssert.assertTrue(c.isCycle());
			int maxCost = 0;
			for(int i = 0; i < c.E(); i++) {
				Path idc = new Path();
				for(int j = 0; j <= i; j++) {
					idc.add(c.getEdge(j));
				}
				for(int j = i; j >= 0; j--) {
					idc.add(c.getEdge(j).getReverse());
				}
				SrPath seg = segmenter.segment(idc);
				maxCost = Math.max(maxCost, seg.getSegmentCost());
			}
			costs.put(maxCost);
		}
		JSONObject results = new JSONObject();
		results.put("originalIGP", costs);
		JSONArray originalCycles = new JSONArray();
		for(SrPath c : minSegCover.getCycles()) {
			originalCycles.put(c.toFileString(g));
		}
		JSONObject originalCover = new JSONObject();
		originalCover.put("source", minSegCover.getSource());
		originalCover.put("maxSeg", minSegCover.getMaxSeg());
		originalCover.put("cycles", originalCycles);
		originalCover.put("runtime", end - start);
		results.put("originalCover", originalCover);
		
		// compute with ECMP-free/complete IGP
		g.setWeightFunction("igp", g.getWeigthFunction("igp_complete"));
		start = System.nanoTime();
		minSegCover = new MinSegCycleCover(g, true);
		end = System.nanoTime();
		segmenter = new Segmenter(g);
		JSONArray costs2 = new JSONArray();
		for(SrPath src : minSegCover.getCycles()) {
			MyAssert.assertTrue(src.isCycle());
			Path c = src.path(g);
			MyAssert.assertTrue(c.isCycle());
			int maxCost = 0;
			for(int i = 0; i < c.E(); i++) {
				Path idc = new Path();
				for(int j = 0; j <= i; j++) {
					idc.add(c.getEdge(j));
				}
				for(int j = i; j >= 0; j--) {
					idc.add(c.getEdge(j).getReverse());
				}
				SrPath seg = segmenter.segment(idc);
				maxCost = Math.max(maxCost, seg.getSegmentCost());
			}
			costs2.put(maxCost);
		}
		results.put("newIGP", costs2);
		JSONArray newCycles = new JSONArray();
		for(SrPath c : minSegCover.getCycles()) {
			newCycles.put(c.toFileString(g));
		}
		JSONObject newCover = new JSONObject();
		newCover.put("source", minSegCover.getSource());
		newCover.put("maxSeg", minSegCover.getMaxSeg());
		newCover.put("cycles", newCycles);
		newCover.put("runetime", end - start);
		results.put("newCover", newCover);
		
		return results;
	}

}
