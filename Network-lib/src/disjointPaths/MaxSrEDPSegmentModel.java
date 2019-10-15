package disjointPaths;

import java.util.ArrayList;
import java.util.LinkedList;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import experiments.RunConfig;
import graph.Graph;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import sr.ForwGraphs;
import sr.SrPath;
import utils.MyAssert;

public class MaxSrEDPSegmentModel {

	private GRBModel model;
	private GRBVar[][][] x;
	private Graph g;
	private ForwGraphs forw;
	private int s, t, maxSeg, D;
	private boolean integral;

	public MaxSrEDPSegmentModel(Graph g, int s, int t, int maxSeg, boolean integral) {
		this.g = g;
		forw = new ForwGraphs(g);
		D = g.outDeg(s);
		this.s = s;
		this.t = t;
		this.maxSeg = maxSeg;
		this.integral = integral;
		buildModel();
	}

	public void buildModel() {
		try {
			// initialize the environment and model
			GRBEnv env = new GRBEnv();
			model = new GRBModel(env);
			model.getEnv().set(GRB.IntParam.OutputFlag, RunConfig.MIP_VERBOSE ? 1 : 0);
			// initialize the model variables
			x = new GRBVar[D][g.V()][g.V()];
			for(int u = 0; u < g.V(); u++) {
				for(int v = 0; v < g.V(); v++) {
					for(int d = 0; d < D; d++) {						
						boolean zero = u == v || v == s || u == t;
						x[d][u][v] = model.addVar(0, zero ? 0 : 1, 0, integral ? GRB.INTEGER : GRB.CONTINUOUS, "x[" + u + "," + v + "," + d + "]");
					}
				}
			}
			model.update();
			// add the model constraints
			// capacity constraints
			for(Edge e : g.getEdgesByIndex()) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						if(u == v) continue;
						if(forw.belongsToIGPShortestPath(u, v, e)) {
							for(int d = 0; d < D; d++) {
								expr.addTerm(1, x[d][u][v]);
							}
						}
					}
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 1, "disjointness[" + e.getIndex() + "]");
			}
			// flow conservation constraints
			for(int d = 0; d < D; d++) {
				for(int u = 0; u < g.V(); u++) {
					if(u == s || u == t) continue;
					GRBLinExpr expr = new GRBLinExpr();
					for(int v = 0; v < g.V(); v++) {
						if(v == u) continue;
						expr.addTerm(1, x[d][v][u]);
						expr.addTerm(-1, x[d][u][v]);
					}
					model.addConstr(expr, GRB.EQUAL, 0, "convervation[" + u + "," + d + "]");
				}
			}
			// segment cost
			for(int d = 0; d < D; d++) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						if(v == u) continue;
						expr.addTerm(1, x[d][u][v]);
					}
				}
				model.addConstr(expr, GRB.LESS_EQUAL, maxSeg, "segcost[" + d + "]");
			}
			model.update();
			// objective
			GRBLinExpr expr = new GRBLinExpr();
			for(int d = 0; d < D; d++) {
				for(int u = 0; u < g.V(); u++) {
					if(u == s) continue;
					expr.addTerm(1, x[d][s][u]);
				}
			}
			model.setObjective(expr, GRB.MAXIMIZE);
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}
	
	/*
	public double optimize(int timeout) {
		try {
			model.set(GRB.DoubleParam.TimeLimit, timeout);
			return optimize();
		} catch (GRBException e) {
			e.printStackTrace();
		}
		MyAssert.assertTrue(false, "could not optimize");
		return 0;
	}
	*/
	

	public double optimize() {
		try {
			model.optimize();
			return model.get(GRB.DoubleAttr.ObjVal);
		} catch (GRBException e) {
			e.printStackTrace();
		}
		MyAssert.assertTrue(false, "could not optimize");
		return 0;
	}

	public ArrayList<SrPath> buildPaths() {
		try {
			int[][][] f = new int[D][g.V()][g.V()];
			for(int d = 0; d < D; d++) {
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						f[d][u][v] = (int)x[d][u][v].get(GRB.DoubleAttr.X);
					}
				}
			}
			ArrayList<SrPath> paths = new ArrayList<>();
			for(int d = 0; d < D; d++) {
				SrPath p = buildPath(f, s, d, new LinkedList<>());
				if(p != null) {
					paths.add(p);					
				}
			}
			return paths;
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SrPath buildPath(int[][][] f, int u, int d, LinkedList<Integer> nodes) {
		nodes.add(u);
		if(u == t) {
			return new SrPath(nodes);
		}
		for(int v = 0; v < g.V(); v++) {
			if(f[d][u][v] == 1) {
				f[d][u][v] = 0;
				return buildPath(f, v, d, nodes);
			}
		}
		return null;
	}


}
