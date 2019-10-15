package monitoring;

import java.util.ArrayList;

import dataStructures.Edge;
import experiments.RunConfig;
import graph.Graph;
import graph.Graphs;
import gurobi.GRB;
import gurobi.GRBColumn;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import sr.Forw;
import sr.SrPath;
import utils.Cmp;
import utils.MyAssert;

public class CycleCoverCGModel {

	private Graph g;
	private Forw forw;
	private int source, maxSeg;
	private GRBModel model;
	private ArrayList<GRBVar> x;
	private ArrayList<SrPath> cycles;
	private ArrayList<GRBConstr> constraints;
	private Edge[] edges;
	private boolean integral;

	public CycleCoverCGModel(Graph g, Forw forw, ArrayList<SrPath> initialCycles, int source, int maxSeg, boolean integral) {
		this.g = g;
		this.forw = forw;
		this.cycles = initialCycles;
		this.edges = g.getEdgesByIndex();
		this.source = source;
		this.maxSeg = maxSeg;
		this.integral = integral;
		createModel();
	}

	public int nbColumns() {
		return cycles.size();
	}

	public CycleCoverCGSolution getSolution() {
		return new CycleCoverCGSolution(g, cycles, getPrimalValues(), source, maxSeg);
	}

	private void createModel() {
		try {
			// initialize the environment and model
			GRBEnv env = new GRBEnv();
			model = new GRBModel(env);
			model.getEnv().set(GRB.IntParam.OutputFlag, RunConfig.MIP_VERBOSE ? 1 : 0);
			// initialize the model variables
			x = new ArrayList<>();
			for(int c = 0; c < cycles.size(); c++) {
				x.add(model.addVar(0, GRB.INFINITY, 1, integral ? GRB.INTEGER : GRB.CONTINUOUS, "x" + c));
			}
			model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
			model.update();
			// add the model constraints
			constraints = new ArrayList<>();
			for(Edge e : edges) {
				GRBLinExpr expr = new GRBLinExpr();
				for(int c = 0; c < cycles.size(); c++) {
					expr.addTerm(I(c, e), x.get(c));
				}
				constraints.add(model.addConstr(expr, GRB.GREATER_EQUAL, 1, "e" + e.getIndex()));
			}
			model.update();
		} catch (GRBException e) {
			e.printStackTrace();
		}
	}

	private int I(int c, Edge e) {
		return I(cycles.get(c), e);
	}

	private int I(SrPath cycle, Edge e) {
		int incidenceCount = 0;
		for(int i = 0; i < cycle.size(); i++) {
			if(cycle.get(i).isAdj() && cycle.get(i).getEdge().getIndex() == e.getIndex()) {
				incidenceCount += 1;
			}
		}
		for(int i = 1; i < cycle.size(); i++) {
			if(forw.dagContainsEdge(cycle.get(i - 1).s2(), cycle.get(i).s1(), e)) {
				incidenceCount += 1;
			}
		}
		return incidenceCount;
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

	public void addCycle(SrPath cycle) {
		try {
			cycles.add(cycle);
			int c = cycles.size() - 1;
			GRBColumn column = new GRBColumn();
			for(int e = 0; e < edges.length; e++) {
				column.addTerm(I(c, edges[e]), constraints.get(e));
			}
			GRBVar var = model.addVar(0, GRB.INFINITY, 1, GRB.CONTINUOUS, column, "x" + c);
			x.add(var);
			model.update();
		} catch(GRBException e) {
			e.printStackTrace();
			MyAssert.assertTrue(false, "could not add cycle");
		}
	}

	public double[] getPrimalValues() {
		try {
			double[] vals = new double[cycles.size()];
			for(int c = 0; c < cycles.size(); c++) {
				vals[c] = x.get(c).get(GRB.DoubleAttr.X);
			}
			return vals;
		} catch(GRBException e) {
			e.printStackTrace();
			MyAssert.assertTrue(false, "could not get primal values");
			return null;
		}
	}

	public double[] getDualValues() {
		try {
			double[] y = new double[edges.length];
			for(int e = 0; e < edges.length; e++) {
				y[e] = constraints.get(e).get(GRB.DoubleAttr.Pi);
			}
			return y;
		} catch(GRBException e) {
			e.printStackTrace();
			MyAssert.assertTrue(false, "could not get dual values");
			return null;
		}
	}


	public void debug() {
		try {
			double[][] a = new double[constraints.size()][x.size()];
			for(int e = 0; e < constraints.size(); e++) {
				GRBConstr constr = constraints.get(e);
				for(int c = 0; c < x.size(); c++) {
					GRBVar var = x.get(c);
					a[e][c] = model.getCoeff(constr, var);
					System.out.printf("%.3f ", + a[e][c]);

				}
				System.out.println();
			}
			System.out.println();
		} catch(GRBException e) {
			e.printStackTrace();
		}
	}

	public boolean isDualAdmissible(double[] y) {
		for(int e = 0; e < constraints.size(); e++) {
			if(!Cmp.geq(y[e], 0)) return false;
		}
		for(int c = 0; c < x.size(); c++) {
			double sum = 0;
			for(int e = 0; e < constraints.size(); e++) {
				sum += I(c, edges[e]) * y[e];
			}
			if(Cmp.gr(sum, 1)) {
				System.out.println("error: not dual admission for c=" + c);
				return false;
			}
		}
		return true;
	}

	public double dualConstraintValue(SrPath cycle, double[] y) {
		double sum = 0;	
		for(int e = 0; e < constraints.size(); e++) {
			int i = I(cycle, edges[e]);
			if(i > 0) {
				System.out.println(edges[e] + " " + i + " " + y[e]);				
			}
			sum += I(cycle, edges[e]) * y[e];
		}
		return sum;
	}

}
