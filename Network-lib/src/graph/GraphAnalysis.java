package graph;

import java.util.Arrays;

public class GraphAnalysis {
	
	private Graph g;
	private boolean undirected, connected;
	private int nbcc;
	
	public GraphAnalysis(Graph g) {
		this.g = g;
		undirected = Graphs.isUndirected(g);
		nbcc = Graphs.nbConenctedComponents(g);
		if(nbcc > 1) connected = false;
	}
	
	public boolean undirected() {
		return undirected;
	}
	
	public boolean connected() {
		return connected;
	}
	
	public int nbConnectedComponents() {
		return nbcc;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		System.out.println("----- Graph: " + g.getName() + " -----");
		System.out.println("V " + g.V());
		System.out.println("E " + g.E());
		
		sb.append("undirected: " + undirected + "\n");
		if(undirected) {
			sb.append("nbCC: " + Graphs.nbConenctedComponents(g) + "\n");
			if(nbcc > 1) {
				sb.append("CC sizes: " + Arrays.toString(Graphs.connectedComponentsSizes(g)) + "\n");
			}
		}
		return sb.toString();
	}

}
