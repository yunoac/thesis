package sr;

import dataStructures.Edge;
import graph.Graph;
import graph.Graphs;
import graph.Path;
import graph.WeightFunction;
import utils.Cmp;

public class Segmenter {
	
	private double[][] igpDist;
	private WeightFunction igp;
	private Graph g;
	
	public Segmenter(Graph g) {
		this.g = g;
		this.igp = g.getWeigthFunction("igp");
		igpDist = Graphs.floydWarshal(g, igp);
	}
	
	public Segmenter(Graph g, double[][] igpDist) {
		this.g = g;
		this.igp = g.getWeigthFunction("igp");
		this.igpDist = igpDist;
	}
	
	public SrPath segment(Path p) {
		int dagRoot = p.firstNode();
		SrPath srPath = new SrPath();
		for(int i = 0; i < p.E(); i++) {
			Edge e = p.getEdge(i);
			int u = e.orig();
			int v = e.dest();
			// the current edge (u, v) is not in the current dag or
	        // it's in degree in the dag is > 1 (ECMP from dag_root to v). 
	        // We need to add some segment.
			if(!edgeInDag(dagRoot, e) || dagInDegree(dagRoot, v) > 1) {
		        // there is no ECMP from u to v, we add node segment u
				if(!edgeInDag(u, e) || dagInDegree(u, v) > 1) {
					// e is not a shortest path or there is ECMP from u to v
					srPath.add(new Segment(e));
					dagRoot = v;
				} else {
					// a node segment to u is enough
					srPath.add(new Segment(u));
					dagRoot = u;
				}
			}
		}
		// add source, if necessary
		if(srPath.size() == 0 || srPath.getFirst().s1() != p.firstNode()) {
			srPath.addFirst(new Segment(p.firstNode()));
		}
		// add destination, if necessary
		if(srPath.getLast().s2() != p.lastNode()) {
			srPath.addLast(new Segment(p.lastNode()));
		}
		srPath.indexPath();
		for(int i = 0; i < srPath.size(); i++) srPath.get(i).setGraph(g);
		return srPath;
	}

	private boolean edgeInDag(int dagRoot, Edge e) {
		return Cmp.eq(igpDist[dagRoot][e.orig()] + igp.getWeight(e), igpDist[dagRoot][e.dest()]);
	}
	
	private int dagInDegree(int dagRoot, int v) {
		int inDeg = 0;
		for(Edge e : g.inEdges(v)) {
			if(edgeInDag(dagRoot, e)) inDeg += 1;
		}
		return inDeg;
	}
	
}
