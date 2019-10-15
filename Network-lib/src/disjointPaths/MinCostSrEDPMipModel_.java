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

public class MinCostSrEDPMipModel_ {

	private GRBModel model;
	private GRBVar[][][] x;
	private Graph g;
	private ForwGraphs forw;
	private int s, t, K;
	private boolean integral;

	public MinCostSrEDPMipModel_(Graph g, int s, int t, int K, boolean integral) {
		this.g = g;
		forw = new ForwGraphs(g);
		this.s = s;
		this.t = t;
		this.K = K;
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
			x = new GRBVar[K][g.V()][g.V()];
			for(int u = 0; u < g.V(); u++) {
				for(int v = 0; v < g.V(); v++) {
					for(int k = 0; k < K; k++) {						
						boolean zero = u == v || (k == K - 1 && v != t) || (k == 0 && u != s) || v == s || u == t;
						x[k][u][v] = model.addVar(0, zero ? 0 : 1, 0, integral ? GRB.INTEGER : GRB.CONTINUOUS, "x[" + u + "," + v + "," + k + "]");
					}
				}
			}
			model.update();
			// add the model constraints
			// capacity constraints
			// /!\ no cycles possible with these constraints
			for(Edge e : g.getEdgesByIndex()) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						if(u == v) continue;
						if(forw.belongsToIGPShortestPath(u, v, e)) {
							for(int k = 0; k < K; k++) {
								expr.addTerm(1, x[k][u][v]);
							}
						}
					}
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 1, "disjointness[" + e.getIndex() + "]");
			}
			// flow conservation constraints
			for(int k = 1; k < K; k++) {
				for(int u = 0; u < g.V(); u++) {
					if(u == s || u == t) continue;
					GRBLinExpr expr = new GRBLinExpr();
					for(int v = 0; v < g.V(); v++) {
						if(v == u) continue;
						expr.addTerm(1, x[k - 1][v][u]);
						expr.addTerm(-1, x[k][u][v]);
					}
					model.addConstr(expr, GRB.EQUAL, 0, "convervation[" + u + "," + k + "]");
				}
			}
			model.update();
			// objective
			GRBLinExpr expr = new GRBLinExpr();
			for(int u = 0; u < g.V(); u++) {
				if(u == s) continue;
				expr.addTerm(1, x[0][s][u]);
			}
			model.setObjective(expr, GRB.MAXIMIZE);
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

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
			int[][][] f = new int[K][g.V()][g.V()];
			for(int k = 0; k < K; k++) {
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						f[k][u][v] = (int)x[k][u][v].get(GRB.DoubleAttr.X);
					}
				}
			}
			ArrayList<SrPath> paths = new ArrayList<>();
			while(true) {
				SrPath p = buildPath(f, s, 0, new LinkedList<>());
				if(p == null) break;
				paths.add(p);
			}
			return paths;
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SrPath buildPath(int[][][] f, int u, int k, LinkedList<Integer> nodes) {
		nodes.add(u);
		if(u == t) {
			return new SrPath(nodes);
		}
		for(int v = 0; v < g.V(); v++) {
			if(f[k][u][v] == 1) {
				f[k][u][v] = 0;
				return buildPath(f, v, k + 1, nodes);
			}
		}
		return null;
	}


}
