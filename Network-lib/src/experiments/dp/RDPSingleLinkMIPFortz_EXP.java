package experiments.dp;

import java.util.HashSet;
import java.util.LinkedList;

import org.json.JSONObject;

import IO.STIO;
import disjointPaths.GenerateST;
import disjointPaths.RDPSingleLinkFortz;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;

public class RDPSingleLinkMIPFortz_EXP extends Experiment {
	
	private int maxSeg;
	private String stName;
	
	public RDPSingleLinkMIPFortz_EXP(String name, boolean skip, boolean write, int maxSeg, String stName) {
		super(name, skip, write);
		this.maxSeg = maxSeg;
		this.stName = stName;
	}

	public static void main(String[] args) {
		Experiment exp = new RDPSingleLinkMIPFortz_EXP("rdpSingleLinkMIPFortz", false, false, 5, "connectedST");
		ExperimentRunner.run(exp, "rf", "1755");
	}

	public JSONObject run(Graph g) {
		
		LinkedList<dem> st = STIO.readST(stName, getCurrentGroup(), g);
		for(int[] x : st) {
			System.out.println(g.getNodeLabel(x[0]) + "->" + g.getNodeLabel(x[2]) + "; " + g.getNodeLabel(x[1]) + "->" + g.getNodeLabel(x[3]));
			RDPSingleLinkFortz rdp = new RDPSingleLinkFortz(g, maxSeg, true);
			HashSet<Integer> S = new HashSet<>();
			S.add(x[0]);
			S.add(x[2]);
			
			HashSet<Integer> T = new HashSet<>();
			T.add(x[1]);
			T.add(x[3]);
			
			Double lat = rdp.optimize(S, T, 2);
			System.out.println(lat);
		}
		return null;
	}

}
