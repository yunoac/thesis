package monitoring;

import java.util.ArrayList;
import java.util.Arrays;

import dataStructures.Edge;
import dataStructures.SrMetric;
import graph.ECMP;
import graph.Graph;
import sr.Forw;
import sr.SrOptim;
import sr.SrPath;
import utils.ArrayExt;
import utils.Cmp;
import utils.MyAssert;

public class CycleCoverCG {

	private Graph g;
	private Edge[] edges;
	private ECMP ecmp;
	private int source, maxSeg;
	private CycleCoverCGModel model;
	private int maxEqIter;
	private Forw forw;
	private ArrayList<SrPath> initialCycles;
	private CGLog log;

	public CycleCoverCG(Graph g, ArrayList<SrPath> initialCycles, int source, int maxSeg, int maxEqIter) {
		this.g = g;
		this.initialCycles = initialCycles;
		this.source = source;
		this.maxSeg = maxSeg;
		this.maxEqIter = maxEqIter;
		edges = g.getEdgesByIndex();
		ecmp = new ECMP(g);
		forw = new Forw(g);
	}
	
	public CycleCoverCGSolution run(boolean verbose) {
		if(verbose) System.out.println("initializing model");
		log = new CGLog();
		model = new CycleCoverCGModel(g, forw, initialCycles, source, maxSeg, false);
		//model.debug();
		
		if(verbose) System.out.println("running");
		
		double[] prevY = null;
		SrPath prevCycle = null;
		double prevValue = Double.POSITIVE_INFINITY;
		
		int equalCount = 1;
		int iterationCount = 1;
		while(true) {
			if(verbose) {
				System.out.println("----- Iteration " + (iterationCount++) + " -----");
				System.out.println("solving model");
				System.out.println("number of  columns: " + model.nbColumns());
			}
			double curValue = model.optimize();
			log.add(curValue);
			if(verbose) {
				System.out.println("previous objective: " + prevValue);
				System.out.println("current  objective: " + curValue);
			}
			if(prevValue == Double.POSITIVE_INFINITY || !Cmp.eq(prevValue, curValue)) {
				equalCount = 1;
			} else {
				if(verbose) System.out.println("! equal iteration");
				equalCount++;
			}
			prevValue = curValue;
			double[] y = model.getDualValues();
			if(verbose) {
				System.out.println("getting dual values");
			}
			if(verbose) System.out.println("solving pricing");
			SrPath cycle = pricing(y);
			if(cycle == null || equalCount > maxEqIter) break;
			
			if(prevCycle != null && cycle.equals(prevCycle)) {
				System.out.println(y.length);
				System.out.println(prevY.length);
				System.out.println(ArrayExt.eq(y, prevY));
				
				System.out.println(model.dualConstraintValue(cycle, y));
				cycle = pricing(y);
				
			}
			
			MyAssert.assertTrue(prevCycle == null || !cycle.equals(prevCycle), "repeated cycle: " + iterationCount + ": " + cycle);

			if(verbose) System.out.println("found cycle with cost: " + cycle.getWeight());
			model.addCycle(cycle);
			prevCycle = cycle;
			prevY = y;
			if(verbose) {
				System.out.println(cycle);				
			}
			
			
		}
		if(verbose) System.out.println("done");
		return model.getSolution();
	}
	
	public CycleCoverCGSolution getIntegralSolution() {
		CycleCoverCGModel ip = new CycleCoverCGModel(g, forw, model.getSolution().getCycles(), source, maxSeg, true);
		return ip.getSolution();
	}

	public CGLog getLog() {
		return log;
	}
	
	private SrPath pricing(double[] y) {
		SrMetric w = new SrMetric(getWeights(y), y);
		SrPath cycle = SrOptim.maxWeightDetSrPath(g, ecmp, source, source, w, maxSeg - 1);	
		double w1 = cycle.getWeight();
		double w2 = weight(cycle, w);
		
		if(!Cmp.eq(w1, w2)) {
			 SrOptim.maxWeightDetSrPath(g, ecmp, source, source, w, maxSeg - 1);
		}
		
		MyAssert.assertTrue(Cmp.eq(w1, w2), "wrong weight " + w1 + " " + w2 + ", " + cycle);
		
		if(Cmp.leq(cycle.getWeight(), 1)) return null;
		return cycle;
	}
	
	private double weight(SrPath p, SrMetric w) {
		double ret = 0;
		for(int i = 0; i < p.size(); i++) {
			if(p.get(i).isAdj()) {
				ret += w.getWeight(p.get(i).getEdge());
			}
		}
		for(int i = 1; i < p.size(); i++) {
			ret += w.getWeight(p.get(i - 1).s2(), p.get(i).s1());
		}
		return ret;
	}

	private double[][] getWeights(double[] y) {
		double[][] w = new double[g.V()][g.V()];
		for(int v = 0; v < g.V(); v++) {
			for(int u = 0; u < g.V(); u++) {
				for(Edge e : edges) {
					if(forw.dagContainsEdge(v, u, e)) {
						if(e.getIndex() >= y.length) {
							System.out.println(e.getIndex() + " " + y.length + " " + g.E());
						}
						w[v][u] += y[e.getIndex()];	
					}
				}
			}
		}
		return w;
	}

}
