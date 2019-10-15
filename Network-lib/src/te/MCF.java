package te;

import java.util.ArrayList;
import java.util.HashMap;

import dataStructures.Edge;
import dataStructures.NodeQueue;
import dataStructures.Pair;
import experiments.RunConfig;
import graph.Dags;
import graph.Graph;
import graph.Path;
import graph.WeightFunction;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import utils.Cmp;
import utils.MyAssert;

public class MCF {

	private TEInstance ins;
	private GRBModel model;
	private GRBVar[][] x;
	private GRBVar L;
	private boolean integral;

	public MCF(TEInstance ins, boolean integral) {
		this.ins = ins;
		this.integral = integral;
		buildModel();
	}

	private void buildModel() {
		try {
			Graph g = ins.getGraph();
			Edge[] edges = g.getEdgesByIndex();
			// initialize the environment and model
			GRBEnv env = new GRBEnv();
			model = new GRBModel(env);
			model.getEnv().set(GRB.IntParam.OutputFlag, RunConfig.MIP_VERBOSE ? 1 : 0);
			// initialize the model variables
			x = new GRBVar[g.E()][ins.D()];
			for(int e = 0; e < g.E(); e++) {
				for(int d = 0; d < ins.D(); d++) {
					x[e][d] = model.addVar(0, 1, 0, integral ? GRB.INTEGER : GRB.CONTINUOUS, "x" + e);
				}
			}
			L = model.addVar(0, GRB.INFINITY, 1, GRB.CONTINUOUS, "L");
			model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
			model.update();
			// add the model constraints
			// capacity constraints
			for(int e = 0; e < g.E(); e++) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int d = 0; d < ins.D(); d++) {
					expr.addTerm(1, x[e][d]);
				}
				double cap = g.getWeight("bdw", edges[e]);
				expr.addTerm(-cap, L);
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "cap[e=" + e + "]");
			}
			// flow conservation constraints
			for(int d = 0; d < ins.D(); d++) {
				Demand demand = ins.getDemand(d);
				for(int v = 0; v < g.V(); v++) {
					GRBLinExpr expr = new GRBLinExpr();
					for(Edge e : g.inEdges(v)) {
						expr.addTerm(1, x[e.getIndex()][d]);
					}
					for(Edge e : g.outEdges(v)) {
						expr.addTerm(-1, x[e.getIndex()][d]);
					}
					int rhs = v == demand.getOrig() ? -1 : v == demand.getDest() ? 1 : 0;
					model.addConstr(expr, GRB.EQUAL, rhs, "conservation[d=" + d + ",v=" + v + "]");
				}
			}
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
		MyAssert.assertTrue(false, "gurobi exception in CycleCoverCGModel.optimize");
		return 0;
	}

	public double getFlow(Edge edge, Demand demand) {
		try {
			return x[edge.getIndex()][demand.getIndex()].get(GRB.DoubleAttr.X);
		} catch (GRBException e1) {
			e1.printStackTrace();
		}
		MyAssert.assertTrue(false);
		return -1;
	}
	
	public double getFlow(int e, int d) {
		try {
			return x[e][d].get(GRB.DoubleAttr.X);
		} catch (GRBException e1) {
			e1.printStackTrace();
		}
		MyAssert.assertTrue(false);
		return -1;
	}

	public Graph getFlowGraph(Demand demand) {
		Graph g = ins.getGraph();
		Graph gf = new Graph(g.nodeLabels());
		NodeQueue Q = new NodeQueue();
		Q.add(demand.getOrig());
		WeightFunction f = new WeightFunction();
		// perform a BFS visiting all path with positive flow from the source of this demand
		while(!Q.isEmpty()) {
			int cur = Q.poll();
			for(Edge edge : g.outEdges(cur)) {
				double xed = getFlow(edge, demand);
				if(!Q.visited(edge.dest()) && Cmp.gr(xed, 0)) {
					// add the edge to the flow graph
					gf.addEdge(edge);
					f.setWeight(edge, xed);
					Q.add(edge.dest());
				}
			}
		}
		return gf;
	}

	public HashMap<Demand, ArrayList<Pair<Path, Double>>> getPathFlows() {
		// loop over each demand and build the paths
		HashMap<Demand, ArrayList<Pair<Path, Double>>> M = new HashMap<>();
		for(int d = 0; d < ins.D(); d++) {
			Demand demand = ins.getDemand(d);
			Graph gf = getFlowGraph(demand);
			ArrayList<Pair<Path, Double>> pd = Dags.pathDecomposition(gf, demand.getOrig(), demand.getDest());
			MyAssert.assertTrue(pd.size() > 0);
			M.put(demand, pd);
		}
		return M;
	}

	public void debug() {
		for(int e = 0; e < ins.E(); e++) {
			for(int d = 0; d < ins.D(); d++) {
				System.out.printf("%.3f ", getFlow(e, d));
			}
			System.out.println();
		}
	}


}
