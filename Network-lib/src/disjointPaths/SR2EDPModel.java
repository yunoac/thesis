package disjointPaths;

import java.util.LinkedList;

import dataStructures.Edge;
import dataStructures.Pair;
import dataStructures.RDPDemand;
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

public class SR2EDPModel {

	private GRBModel model;
	private GRBVar[][][] x;
	private GRBVar L;
	private Graph g;
	private ForwGraphs forw;
	private int maxSeg, D;
	private RDPDemand demand;
	private boolean integral;

	public SR2EDPModel(Graph g, RDPDemand demand, int maxSeg, boolean integral) {
		this.g = g;
		forw = new ForwGraphs(g);
		this.demand = demand;
		this.maxSeg = maxSeg;
		this.integral = integral;
		D = 2;
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
						boolean zero = u == v || v == demand.getS(d) || u == demand.getT(d);
						x[d][u][v] = model.addVar(0, zero ? 0 : 1, 0, integral ? GRB.INTEGER : GRB.CONTINUOUS, "x[" + u + "," + v + "," + d + "]");
					}
				}
			}			
			double latSum = 0;
			for(Edge e : g.getEdgesByIndex()) {
				latSum += g.getWeight("lat", e);
			}
			L = model.addVar(0, latSum, 1, GRB.CONTINUOUS, "lambda");
			model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
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
					if(demand.getS(d) == u || demand.getT(d) == u) continue;
					GRBLinExpr expr = new GRBLinExpr();
					for(int v = 0; v < g.V(); v++) {
						if(v == u) continue;
						expr.addTerm(1, x[d][v][u]);
						expr.addTerm(-1, x[d][u][v]);
					}
					model.addConstr(expr, GRB.EQUAL, 0, "convervation[" + u + "," + d + "]");
				}
			}
			// max cost constraint
			for(int d = 0; d < D; d++) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						if(v == u) continue;
						expr.addTerm(forw.getForwLat(u, v), x[d][u][v]);
					}
				}
				expr.addTerm(-1, L);
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "cost_constraint[" + d + "]");
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
				model.addConstr(expr, GRB.LESS_EQUAL, maxSeg - 1, "segcost[" + d + "]");
			}
			model.update();
			// out flow constraints
			for(int d = 0; d < D; d++) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					if(u == demand.getS(d)) continue;
					expr.addTerm(1, x[d][demand.getS(d)][u]);
				}
				model.addConstr(expr, GRB.EQUAL, 1, "out_s[" + d + "]");
			}
			// in flow constraints
			for(int d = 0; d < D; d++) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					if(u == demand.getT(d)) continue;
					expr.addTerm(1, x[d][u][demand.getT(d)]);
				}
				model.addConstr(expr, GRB.EQUAL, 1, "in_t[" + d + "]");
			}
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	public void setValue(int d, int u, int v, int value) {
		GRBLinExpr expr = new GRBLinExpr();
		expr.addTerm(1, x[d][u][v]);
		try {
			model.addConstr(expr, GRB.EQUAL, value, "value[" + d + "][" + u + "][" + v + "]");			
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	public Double optimize() {
		try {
			model.optimize();
			int status = model.get(GRB.IntAttr.Status);
			if(status == GRB.Status.INFEASIBLE) {
				//model.computeIIS();
				//model.write("model.ilp");
				return null;
			}
			return model.get(GRB.DoubleAttr.ObjVal);
		} catch (GRBException e) {
			e.printStackTrace();
		}
		MyAssert.assertTrue(false, "could not optimize");
		return null;
	}

	public Pair<SrPath, SrPath> buildPaths() {
		try {
			int status = model.get(GRB.IntAttr.Status);
			if(status == GRB.Status.INFEASIBLE) {
				return null;
			}
			int[][][] f = new int[D][g.V()][g.V()];
			for(int d = 0; d < D; d++) {
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						f[d][u][v] = (int)Math.round(x[d][u][v].get(GRB.DoubleAttr.X));
					}
				}
			}
			SrPath p1 = buildPath(f, demand.s1(), demand.t1(), 0, 0.0, new LinkedList<>());
			SrPath p2 = buildPath(f, demand.s2(), demand.t2(), 1, 0.0, new LinkedList<>());
			return new Pair<>(p1, p2);
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SrPath buildPath(int[][][] f, int u, int t, int d, double lat, LinkedList<Integer> nodes) {
		nodes.add(u);
		if(u == t) {
			SrPath p = new SrPath(nodes);
			p.indexPath();
			p.setWeight(lat);
			return p;
		}
		for(int v = 0; v < g.V(); v++) {
			if(f[d][u][v] == 1) {
				f[d][u][v] = 0;
				return buildPath(f, v, t, d, lat + forw.getForwLat(u, v), nodes);
			}
		}
		return null;
	}


}
