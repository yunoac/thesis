package experiments.dp;

import java.util.LinkedList;

import javax.naming.ldap.Rdn;

import org.json.JSONObject;

import IO.STIO;
import dataStructures.Pair;
import dataStructures.RDPDemand;
import disjointPaths.GenerateST;
import disjointPaths.SR2DisjointPaths;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import sr.SrPath;

public class SR2DisjointPathsEXP extends Experiment {
	
	private int maxSeg;
	
	public SR2DisjointPathsEXP(String name, boolean skip, boolean write, int maxSeg) {
		super(name, skip, write);
		this.maxSeg = maxSeg;
	}

	public static void main(String[] args) {
		Experiment exp = new SR2DisjointPathsEXP("sr2DisjointPaths", true, false, 6);
		//ExperimentRunner.run(exp, "rf", "1755");
		ExperimentRunner.run2(exp, "other", "grid5x5.ntfl");
		
	}

	public JSONObject run(Graph g) {
		SR2DisjointPaths srdp = new SR2DisjointPaths(g);
		//LinkedList<RDPDemand> demands = STIO.readST("biconnectedST", getCurrentGroup(), g);
		//LinkedList<RDPDemand> demands = GenerateST.generateSSTTBiconnected(g, 100);
		LinkedList<RDPDemand> demands = new LinkedList<>();
		demands.add(new RDPDemand(0, 23, 1, 24));
		for(RDPDemand d : demands) {
			System.out.println(d);
			Pair<SrPath, SrPath> paths = srdp.computePaths(d, maxSeg);
			if(paths == null) {
				System.out.println("no solution");
			} else {
				System.out.println(paths);
			}
		}
		return null;
	}

}
