package graph;

import java.util.Comparator;

/*
 * Class used to compare vertices by distance.
 */
public class VertexDistCmp implements Comparator<Integer> {
	
	private double[] distance;
	
	public VertexDistCmp(double[] distance) {
		this.distance = distance;
	}
	
	public int compare(Integer o1, Integer o2) {
		int dcmp = Double.compare(distance[o1], distance[o2]);
		if(dcmp == 0) return o1 - o2;
		return dcmp;
	}
	
}