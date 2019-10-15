package experiments.dp;

import java.util.BitSet;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import disjointPaths.GenerateST;
import disjointPaths.MaxSrEDPFortzModel;
import experiments.Experiment;
import experiments.ExperimentRunner;
import experiments.Generator;
import experiments.GeneratorRunner;
import graph.Graph;
import graph.Graphs;
import graph.MaximumEDP;
import graph.TopologicalSort;
import utils.MyAssert;

public class GenerateST_GEN extends Generator {
	

	public GenerateST_GEN(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Generator gen = new GenerateST_GEN("generateST", false, true);
		GeneratorRunner.generate(gen, "rf");
	}

	public JSONObject generate(Graph g) {
		return GenerateST.generateSSTTBiconnected(g, 100);
	}

}
