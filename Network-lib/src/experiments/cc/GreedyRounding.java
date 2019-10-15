package experiments.cc;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Pair;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import monitoring.CoverChecker;
import monitoring.CycleCoverCG;
import monitoring.CycleCoverCGSolution;
import monitoring.CycleCoverRounder;
import monitoring.MinSegCycleCover;
import sr.Segment;
import sr.SrPath;
import utils.MyAssert;

public class GreedyRounding extends Experiment {
	
	public GreedyRounding(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new GreedyRounding("cycleCover", false, true);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		long t0 = System.nanoTime();
		MinSegCycleCover minSegCover = new MinSegCycleCover(g, true);
		CycleCoverCG cccg = new CycleCoverCG(g, minSegCover.getCycles(), minSegCover.getSource(), minSegCover.getMaxSeg(), Integer.MAX_VALUE);
		CycleCoverCGSolution cover = cccg.run(true);
		ArrayList<SrPath> lpCycles = cover.getCycles();
		ArrayList<SrPath> greedyCycles = CycleCoverRounder.greedyRounder(g, lpCycles);
		long t1 = System.nanoTime();
		Pair<Boolean, String> ok = CoverChecker.checkCycleCover(minSegCover);
		MyAssert.assertTrue(ok);
		ok = CoverChecker.checkCycleCover(greedyCycles, g, minSegCover.getSource(), minSegCover.getMaxSeg());
		MyAssert.assertTrue(ok);
		
		
		JSONObject data = new JSONObject();
		data.put("initialCoverSize", minSegCover.nbCycles());
		data.put("initialCoverSeg", minSegCover.getMaxSeg());
		data.put("lpBound", cccg.getLog().lpBound());			
		data.put("values", new JSONArray(cccg.getLog().valuesToArray()));
		data.put("times", new JSONArray(cccg.getLog().timesToArray()));
		data.put("finalCoverSize", cover.nbCycles());
		data.put("greedyCoverSize", greedyCycles.size());
		data.put("runtime", t1 - t0);
		data.put("lpSolution", cover.toJSON());
		
		JSONArray jsonGreedy = new JSONArray();
		for(SrPath c : greedyCycles) {
			String s = "";
			for(int i = 0; i < c.size(); i++) {
				Segment seg = c.get(i);
				s += g.getNodeLabel(seg.s1());
				if(seg.isAdj()) {
					s += "@" + g.getNodeLabel(seg.s2());
				}
				if(i < c.size() - 1) {
					s += " ";
				}
			}
			jsonGreedy.put(s);
		}
		data.put("greedySolution", jsonGreedy);
		
		JSONArray jsonMinSeg = new JSONArray();
		for(SrPath c : minSegCover.getCycles()) {
			String s = "";
			for(int i = 0; i < c.size(); i++) {
				Segment seg = c.get(i);
				s += g.getNodeLabel(seg.s1());
				if(seg.isAdj()) {
					s += "@" + g.getNodeLabel(seg.s2());
				}
				if(i < c.size() - 1) {
					s += " ";
				}
			}
			jsonMinSeg.put(s);
		}
		data.put("minSegCycles", jsonMinSeg);
		
		return data;
	}

}
