package experiments;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import org.json.JSONObject;

import IO.GraphIO;
import dataStructures.Edge;
import experiments.Experiment;
import experiments.ExperimentRunner;
import graph.Graph;
import graph.Path;
import monitoring.MinSegCycleCover;
import sr.SrPath;
import utils.MyAssert;

public class CheckSymmetry extends Experiment {
	
	public CheckSymmetry(String name, boolean skip, boolean write) {
		super(name, skip, write);
	}

	public static void main(String[] args) {
		Experiment exp = new CheckSymmetry(null, false, false);
		ExperimentRunner.run(exp);
	}

	public JSONObject run(Graph g) {
		Edge[] edges = g.getEdgesByIndex();
		int[] rev = new int[g.E()];
		Arrays.fill(rev, -1);
		
		for(int i = 0; i < edges.length; i++) {
			rev[edges[i].getIndex()] = edges[i].getReverse().getIndex();
			Edge e = edges[i];
			Edge r = e.getReverse();
			MyAssert.assertTrue(e.orig() == r.dest() & e.dest() == r.orig());
		}
		
		for(int i = 0; i < edges.length; i++) {
			MyAssert.assertTrue(rev[i] != -1);
		}
		
		/*
		
		for(int i = 0; i < edges.length; i++) {
			if(rev[i] != -1) continue;
			Edge ei = edges[i];
			for(int j = 0; j < edges.length; j++) {
				if(i == j) continue;
				Edge ej = edges[j];
				if(ei.orig() == ej.dest() && ei.dest() == ej.orig() && rev[j] == -1) {
					rev[i] = j;
					rev[j] = i;
					break;
				}
			}
		}
		for(int i = 0; i < rev.length; i++) {
			MyAssert.assertTrue(rev[i] != -1);
			edges[i].setReverse(edges[rev[i]]);
		}
		MyAssert.assertTrue(getCurrentGroup() != null);
		GraphIO.writeJSON(getCurrentGroup(), g);
		*/
		return null;
	}

}
