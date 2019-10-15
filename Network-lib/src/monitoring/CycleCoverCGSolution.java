package monitoring;

import java.util.ArrayList;
import java.util.BitSet;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.GraphIO;
import dataStructures.Edge;
import experiments.RunConfig;
import graph.Graph;
import gurobi.GRB;
import gurobi.GRBColumn;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import sr.Segment;
import sr.Segmenter;
import sr.SrPath;
import utils.ArrayExt;
import utils.Cmp;
import utils.MyAssert;

public class CycleCoverCGSolution {

	private Graph g;
	private ArrayList<SrPath> cycles;
	private double[] x;
	private int maxSeg;
	private int source;
	
	public CycleCoverCGSolution(Graph g, ArrayList<SrPath> cycles, double[] x, int source, int maxSeg) {
		this.g = g;
		this.cycles = cycles;
		this.source = source;
		this.maxSeg = maxSeg;
		this.x = x;
	}
	
	public Graph getG() {
		return g;
	}
	
	public int nbCycles() {
		int nbCycles = 0;
		for(int i = 0; i < cycles.size(); i++) {
			if(Cmp.gr(x[i], 0)) {
				nbCycles += 1;
			}
		}
		return nbCycles;
	}
	
	public int getSource() {
		return source;
	}
	
	public ArrayList<SrPath> getCycles() {
		return cycles;
	}
	
	public int getMaxSeg() {
		return maxSeg;
	}
	
	public double LPbound() {
		return ArrayExt.sum(x);
	}
	
	public boolean isIntegral() {
		for(int i = 0; i < x.length; i++) {
			if(!Cmp.eq(x[i], 0) && !Cmp.eq(x[i], 1)) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<SrPath> getNonZeroCycles() {
		ArrayList<SrPath> nonZeroCycles = new ArrayList<>();
		for(int i = 0; i < cycles.size(); i++) {
			if(Cmp.gr(x[i], 0)) {
				nonZeroCycles.add(cycles.get(i));
			}
		}
		return nonZeroCycles;
	}
	
	public JSONArray toJSON() {
		JSONArray a = new JSONArray();
		for(int i = 0; i < cycles.size(); i++) {
			JSONObject o = new JSONObject();
			o.put("x", x[i]);
			StringBuilder sb = new StringBuilder();
			SrPath cycle = cycles.get(i);
			for(int j = 0; j < cycle.size(); j++) {
				Segment seg = cycle.get(j);
				sb.append(g.getNodeLabel(seg.s1()));
				if(seg.isAdj()) {
					sb.append("@");
					sb.append(g.getNodeLabel(seg.s2()));
				}
				if(j < cycle.size() - 1) {
					sb.append(" ");
				}
			}
			o.put("cycle", sb.toString());
			a.put(o);
		}
		return a;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < cycles.size(); i++) {
			if(Cmp.gr(x[i], 0)) {
				sb.append(String.format("%.3f %s\n", x[i], cycles.get(i)));
			}
		}
		return sb.toString();
	}
	
}
