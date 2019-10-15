package experiments.dp;

import java.util.BitSet;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import disjointPaths.MaxSrEDPFortzModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Graphs;
import graph.MaximumEDP;
import graph.TopologicalSort;
import utils.MyAssert;

public class PrecomputeForwRuntimeEXP extends Experiment {
	

	public PrecomputeForwRuntimeEXP(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new PrecomputeForwRuntimeEXP("forwPrecompute", true, true);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		JSONObject result = new JSONObject();
		double[] igp = g.getWeigthFunction("igp").toArray();
		Edge[] edges = g.getEdgesByIndex();

		long start1 = System.nanoTime();
		BitSet[][] fw1 = new BitSet[g.V()][g.V()];
		for(int u = 0; u < g.V(); u++) for(int v = 0; v < g.V(); v++) fw1[u][v] = new BitSet();
		for(int u = 0; u < g.V(); u++) {
			Graph spu = Graphs.dijkstraDag(g, igp, u);
			for(int v = 0; v < g.V(); v++) {
				NodeQueue Q = new NodeQueue();
				Q.add(v);
				while(!Q.isEmpty()) {
					int cur = Q.poll();
					for(Edge e : spu.inEdges(cur)) {
						fw1[u][v].set(e.getIndex());
						if(!Q.visited(e.orig())) {
							Q.add(e.orig());
						}
					}
				}
			}
		}
		long end1 = System.nanoTime();
		result.put("filter", end1 - start1);
		
		long start2 = System.nanoTime();
		BitSet[][] fw2 = new BitSet[g.V()][g.V()];
		for(int u = 0; u < g.V(); u++) for(int v = 0; v < g.V(); v++) fw2[u][v] = new BitSet();
		for(int u = 0; u < g.V(); u++) {
			Graph spu = Graphs.dijkstraDag(g, igp, u);
			int[] order = new TopologicalSort(spu).order();
			for(int v : order) {
				for(Edge e : spu.inEdges(v)) {
					fw2[u][v].or(fw2[u][e.orig()]);
					fw2[u][v].set(e.getIndex());
				}
			}
		}
		long end2 = System.nanoTime();
		result.put("toposort", end2 - start2);
		
		return result;
	}

}
