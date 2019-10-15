package dataStructures;

import graph.WeightFunction;
import sr.Segment;

public class SrMetric {

	private double[][] wseg;
	private double[] wadj;
	
	public SrMetric(double[][] wseg, WeightFunction wadj) {
		this.wseg = wseg;
		this.wadj = wadj.toArray();
	}
	
	public SrMetric(double[][] wseg, double[] wadj) {
		this.wseg = wseg;
		this.wadj = wadj;
	}
	
	public double getWeight(Segment s) {
		if(s.isAdj()) return wadj[s.getEdge().getIndex()];
		return wseg[s.s1()][s.s2()];
	}
	
	public double getWeight(int s1, int s2) {
		return wseg[s1][s2];
	}
	
	public double getWeight(Edge e) {
		return wadj[e.getIndex()];
	}

}
