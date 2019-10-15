package disjointPaths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import dataStructures.Edge;
import experiments.RunConfig;
import graph.BiconnectedComponents;
import graph.Graph;
import gurobi.GRB;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import sr.FailGraphs;
import sr.ForwGraphs;
import sr.SrPath;
import utils.MyAssert;

public class RDPSingleLinkFortz {

	private GRBModel model;
	private GRBVar[][][] x;
	private Graph g;
	private ForwGraphs forw;
	private FailGraphs fail;
	private int K, maxPath;
	private boolean integral;
	private BiconnectedComponents bcc;
	private GRBConstr[][] conservation;
	private ArrayList<GRBConstr> added;
	private Collection<Integer> S, T;

	public RDPSingleLinkFortz(Graph g, int K, boolean integral) {
		this.g = g;
		forw = new ForwGraphs(g);
		fail = new FailGraphs(g, forw);
		bcc = new BiconnectedComponents(g);
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
						boolean zero = !bcc.sameBcc(u, v);
						x[k][u][v] = model.addVar(0, zero ? 0 : 1, forw.getForwLat(u, v), integral ? GRB.INTEGER : GRB.CONTINUOUS, "x[" + u + "," + v + "," + k + "]");
					}
				}
			}
			model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
			model.update();
			// add the model constraints
			// capacity constraints
			for(Edge e : g.getEdgesByIndex()) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int u = 0; u < g.V(); u++) {
					for(int v = 0; v < g.V(); v++) {
						if(u == v) continue;
						if(forw.getForwE(u, v).get(e.getIndex()) || fail.getFailE(u, v).get(e.getIndex())) {
							for(int k = 0; k < K; k++) {
								expr.addTerm(1, x[k][u][v]);
							}
						}
					}
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 1, "disjointness[" + e.getIndex() + "]");
			}
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	public void setSourceDest(Collection<Integer> S, Collection<Integer> T, int maxPath) {
		try {
			this.S = S;
			this.T = T;
			this.maxPath = maxPath;
			if(added == null) {
				added = new ArrayList<>();
			} else {	
				// remove added constraints
				for(GRBConstr c : added) {
					model.remove(c);
				}
				added.clear();
			}

			// reset conservation
			if(conservation != null) {
				for(int k = 1; k < K; k++) {
					for(int u = 0; u < g.V(); u++) {
						if(conservation[k][u] != null) {							
							model.remove(conservation[k][u]);
						}
					}
				}
			}

			conservation = new GRBConstr[K][g.V()];
			// flow conservation constraints
			for(int k = 1; k < K; k++) {
				for(int u = 0; u < g.V(); u++) {
					if(S.contains(u) || T.contains(u)) continue;
					GRBLinExpr expr = new GRBLinExpr();
					for(int v = 0; v < g.V(); v++) {
						if(v == u) continue;
						expr.addTerm(1, x[k - 1][v][u]);
						expr.addTerm(-1, x[k][u][v]);
					}
					conservation[k][u] = model.addConstr(expr, GRB.EQUAL, 0, "convervation[" + u + "," + k + "]");
				}
			}

			// no flow into S
			GRBLinExpr expr = new GRBLinExpr();
			for(int u = 0; u < g.V(); u++) {
				for(int k = 0; k < K; k++) {
					for(int s : S) {
						expr.addTerm(1, x[k][u][s]);						
					}
				}
			}
			added.add(model.addConstr(expr, GRB.EQUAL, 0, "no flow into S"));
			// no flow out of T
			expr = new GRBLinExpr();
			for(int u = 0; u < g.V(); u++) {
				for(int k = 0; k < K; k++) {
					for(int t : T) {
						expr.addTerm(1, x[k][t][u]);						
					}
				}
			}
			added.add(model.addConstr(expr, GRB.EQUAL, 0, "no flow out of T"));
			// all flow starts from S
			expr = new GRBLinExpr();
			for(int u = 0; u < g.V(); u++) {
				if(!S.contains(u)) {
					for(int v = 0; v < g.V(); v++) {
						expr.addTerm(1, x[0][u][v]);						
					}
				}
			}
			added.add(model.addConstr(expr, GRB.EQUAL, 0, "all flow starts from S"));
			// all flow ends at T
			expr = new GRBLinExpr();
			for(int u = 0; u < g.V(); u++) {
				for(int v = 0; v < g.V(); v++) {
					if(!T.contains(v)) {
						expr.addTerm(1, x[K - 1][u][v]);						
					}
				}
			}
			added.add(model.addConstr(expr, GRB.EQUAL, 0, "all flow starts from T"));
			// number of paths
			expr = new GRBLinExpr();
			for(int s : S) {
				for(int u = 0; u < g.V(); u++) {
					expr.addTerm(1, x[0][s][u]);
				}
			}
			added.add(model.addConstr(expr, GRB.EQUAL, maxPath, "number of disjoint paths"));
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	public Double optimize(Collection<Integer> S, Collection<Integer> T, int maxPath) {
		try {
			setSourceDest(S, T, maxPath);
			model.optimize();
			int status = model.get(GRB.IntAttr.Status);
			if(status == GRB.Status.INFEASIBLE) {
				return null;
			}
			return model.get(GRB.DoubleAttr.ObjVal);
		} catch (GRBException e) {
			e.printStackTrace();
		}
		MyAssert.assertTrue(false, "could not optimize");
		return null;
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
			for(int s : S) {
				while(true) {
					SrPath p = buildPath(f, s, 0, new LinkedList<>());
					if(p == null) break;
					paths.add(p);
				}
			}
			return paths;
		} catch (GRBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SrPath buildPath(int[][][] f, int u, int k, LinkedList<Integer> nodes) {
		nodes.add(u);
		if(T.contains(u)) {
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
