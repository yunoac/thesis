package graph;

import java.util.Collection;
import java.util.LinkedList;

import dataStructures.Edge;

public class FlowGraph {

	private LinkedList<FlowEdge>[] out;
	private int universalS, universalT;

	@SuppressWarnings("unchecked")
	public FlowGraph(Graph g) {
		out = new LinkedList[g.V()];
		for(int v = 0; v < g.V(); v++) {
			out[v] = new LinkedList<>();
		}
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				FlowEdge e1 = new FlowEdge(e, e.orig(), e.dest(), 1, g.getWeight("lat", e));
				FlowEdge e2 = new FlowEdge(null, e.dest(), e.orig(), 0, -g.getWeight("lat", e));
				e1.setResidual(e2);
				out[e1.orig].add(e1);
				out[e2.orig].add(e2);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public FlowGraph(Graph g, Collection<Integer> S, Collection<Integer> T) {
		out = new LinkedList[g.V() + S.size() + T.size()];
		for(int i = 0; i < out.length; i++) {
			out[i] = new LinkedList<>();
		}
		universalS = g.V();
		universalT = g.V() + 1;
		for(int v = 0; v < g.V(); v++) {
			for(Edge e : g.outEdges(v)) {
				FlowEdge fe = new FlowEdge(e, e.orig(), e.dest(), 1, g.getWeight("lat", e));
				addEdge(fe);
			}
		}
		for(int s : S) {
			FlowEdge e = new FlowEdge(null, universalS, s, g.outDeg(s), 0);
			addEdge(e);
		}
		for(int t : T) {
			FlowEdge e = new FlowEdge(null, t, universalT, g.inDeg(t), 0);
			addEdge(e);
		}
	}
	
	public int getS() {
		return universalS;
	}
	
	public int getT() {
		return universalT;
	}

	public void addEdge(FlowEdge e) {
		FlowEdge r = new FlowEdge(null, e.dest, e.orig, 0, -e.cost);
		e.setResidual(r);
		out[e.orig].add(e);
		out[r.orig].add(r);
	}
	
	public int V() {
		return out.length;
	}

	public LinkedList<FlowEdge> outEdges(int v) {
		return out[v];
	}

	public class FlowEdge {

		private int orig, dest, flow, cap;
		private double cost;
		private FlowEdge residual;
		private Edge original;

		public FlowEdge(Edge original, int orig, int dest, int cap, double cost) {
			this.original = original;
			this.orig = orig;
			this.dest = dest;
			this.cap = cap;
			this.cost = cost;
		}

		public Edge getOriginalEdge() {
			return original;
		}

		public void setResidual(FlowEdge residual) {
			this.residual = residual;
			residual.residual = this;
		}

		public int getCap() {
			return cap;
		}

		public int getFlow() {
			return flow;
		}

		public void addFlow(int delta) {
			this.flow += delta;
		}

		public double getCost() {
			return cost;
		}

		public int orig() {
			return orig;
		}

		public int dest() {
			return dest;
		}

		public FlowEdge getResidual() {
			return residual;
		}

		public double push(int flow) {
			this.flow += flow;
			this.cap -= flow;
			this.residual.flow -= flow;
			this.residual.cap += flow;
			return flow * cost;
		}

		public String toString() {
			return String.format("(%d, %d) %d/%d", orig, dest, flow, cap);
		}

	}

}
