package experiments;

import java.util.ArrayList;

import org.json.JSONObject;

import IO.GraphIO;
import IO.JsonIO;
import dataStructures.Edge;
import dataStructures.Pair;
import graph.Graph;
import graph.Graphs;
import monitoring.CoverChecker;
import monitoring.CycleCoverCG;
import monitoring.CycleCoverCGSolution;
import monitoring.MinSegCycleCover;
import sr.SrPath;
import utils.MyAssert;

public class MinCycleCover2 {
	
	public static void main(String[] args) {
		
		String groupname = "rf";
		String instance = "1239.ntfl";
		/*
		
		String groupname = "other";
		String instance = "grid5x5.ntfl";
		 */
		
		Graph g = GraphIO.read(groupname, instance);
		for(Edge e : g.getEdgesByIndex()) {
			MyAssert.assertTrue(g.getWeight("igp", e) > 0);
		}
		
		
		/*
		
		JSONObject res = JsonIO.read(groupname, g.getName(), "minSegCover");
		
		int source = res.getInt("source");
		int maxSeg = res.getInt("numberOfSegments");
		
		System.out.println(source);
		System.out.println(maxSeg);
		*/
		
		MinSegCycleCover minSegCover = new MinSegCycleCover(g, true);

		
		Pair<Boolean, String> ok = CoverChecker.checkCycleCover(minSegCover);
		MyAssert.assertTrue(ok.first());
		
		CycleCoverCG cccg = new CycleCoverCG(g, minSegCover.getCycles(), minSegCover.getSource(), minSegCover.getMaxSeg(), Integer.MAX_VALUE);
		
		CycleCoverCGSolution cover = cccg.run(true);
		System.out.println(cover.isIntegral());
		//System.out.println(cover);
		ok = CoverChecker.checkCycleCover(minSegCover);
		System.out.println(ok);
	}
	
	public static void cycle5() {
	
	}

}
