package disjointPaths;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import experiments.RunConfig;
import graph.Graph;
import graph.Graphs;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import sr.ForwGraphs;
import sr.SrPath;
import utils.MyAssert;

public class RDPFortz {

	private GRBModel model;
	private GRBVar[][][] x;
	private Graph g;
	private ForwGraphs forw;
	private int s, t, K, nbp;
	private boolean integral;
	private BitSet[] F;

	public RDPFortz(Graph g, int s, int t, int K, BitSet[] F, int nbp, boolean integral) {
		this.g = g;
		forw = new ForwGraphs(g);
		this.s = s;
		this.t = t;
		this.K = K;
		this.F = F;
		this.nbp = nbp;
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
					// check whether there is still a path from u to v after every failure
					boolean uvOk = true;
					for(BitSet f : F) {
						if(forw.getForwE(u, v).intersects(f) && !Graphs.connected(g, u, v, f)) {
							uvOk = false;
							break;
						}
					}
					for(int k = 0; k < K; k++) {						
						boolean zero = !uvOk || u == v || (k == K - 1 && v != t) || (k == 0 && u != s) || v == s || u == t;
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
						if(canTouch(u, v, e)) {
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
			// number of paths
			GRBLinExpr expr = new GRBLinExpr();
			for(int u = 0; u < g.V(); u++) {
				if(u == s) continue;
				expr.addTerm(1, x[0][s][u]);
			}
			model.addConstr(expr, GRB.EQUAL, nbp, "nbp");
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	private boolean canTouch(int u, int v, Edge e) {
		if(forw.belongsToIGPShortestPath(u, v, e)) {
			return true;
		}
		for(BitSet f : F) {
			if(forw.getForwE(u, v).intersects(f)) {
				// check whether there is a shortest path from u to v in G \ f that contains e
				Pair<double[], LinkedList<Edge>[]> p = Graphs.dijsktraAllParents(g, "igp", u);
				LinkedList<Edge>[] parents = p.second();
				NodeQueue Q = new NodeQueue();
				Q.add(v);
				while(!Q.isEmpty()) {
					int cur = Q.poll();
					for(Edge parent : parents[cur]) {
						if(parent.getIndex() == e.getIndex()) return true;
						if(!Q.visited(parent.orig())) {
							Q.add(parent.orig());
						}
					}
				}
			}
		}
		return false;
	}

	public Double optimize() {
		try {
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
