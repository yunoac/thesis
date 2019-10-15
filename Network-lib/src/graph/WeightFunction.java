package graph;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import dataStructures.Edge;

public class WeightFunction {
	
	private HashMap<Integer, Double> weight;
	
	public WeightFunction() {
		weight = new HashMap<>();
	}
	
	public WeightFunction(double[] w) {
		weight = new HashMap<>();
		for(int i = 0; i < w.length; i++) {
			weight.put(i, w[i]);
		}
	}
	
	public WeightFunction(Graph g, double c) {
		weight = new HashMap<>();
		for(Edge e : g.getEdgesByIndex()) {
			setWeight(e, c);
		}
	}
	
	public WeightFunction deepCopy() {
		WeightFunction cp = new WeightFunction();
		for(Entry<Integer, Double> e : weight.entrySet()) {
			cp.weight.put(e.getKey(), e.getValue());
		}
		return cp;
	}
	
	public double maxWeight() {
		double max = Double.NEGATIVE_INFINITY;
		for(Double w : weight.values()) {
			max = Math.max(max, w);
		}
		return max;
	}
	
	public void setWeight(Edge edge, double weight) {
		this.weight.put(edge.getIndex(), weight);
	}
	
	public double getWeight(Edge edge) {
		return weight.get(edge.getIndex());
	}

	public double[] toArray() {
		Set<Entry<Integer, Double>> entries = weight.entrySet();
		int maxIndex = 0;
		for(Entry<Integer, Double> e : entries) {
			maxIndex = Math.max(maxIndex, e.getKey());
		}
		double[] a = new double[maxIndex + 1];
		for(Entry<Integer, Double> e : entries) {
			a[e.getKey()] = e.getValue();
		}
		return a;
	}
	
	public String toString() {
		return weight.toString();
	}
	
}
