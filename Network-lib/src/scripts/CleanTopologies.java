package scripts;

import java.io.File;
import java.util.ArrayList;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Edge;
import graph.Graph;
import graph.Graphs;
import graph.WeightFunction;
import utils.MyAssert;

public class CleanTopologies {

	/*
	 * Execute to create a json file for the topology.
	 */
	public static void main(String[] args) {
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			// loop over topologies in group
			String groupname = group.toString().split("/")[3];
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				if(f.toString().endsWith(".json")) continue;
				// initialize global graph data
				Graph g = GraphIO.read(f);
				String name = g.getName();
				if(Graphs.nbSCC(g) > 1) {
					System.out.println(g.getName() + " has more than one scc");
				}
				g = Graphs.largestSCC(g);
		
				boolean selfLoop = false;
				Graph h = new Graph(g.nodeLabels());
				WeightFunction igp = new WeightFunction();
				WeightFunction lat = new WeightFunction();
				WeightFunction bdw = new WeightFunction();
				int index = 0;
				for(Edge e : g.getEdgesByIndex()) {
					if(e.orig() != e.dest()) {
						Edge eh = new Edge(e.orig(), e.dest(), index);
						h.addEdge(eh);
						igp.setWeight(eh, g.containsWeightFunction("igp") ? g.getWeight("igp", e) : 1);
						lat.setWeight(eh, g.containsWeightFunction("lat") ? g.getWeight("lat", e) : 1);
						bdw.setWeight(eh, g.containsWeightFunction("bdw") ? g.getWeight("bdw", e) : 1);
						
						index += 1;
					} else {
						selfLoop = true;
					}
				}
				if(selfLoop) {
					System.out.println(g.getName() + " has self loops");					
				}
				h.addWeightFunction("igp", igp);
				h.addWeightFunction("lat", lat);
				h.addWeightFunction("bdw", bdw);
				
				h.setName(name);
				
				g = h;
				
				MyAssert.assertTrue(Graphs.nbSCC(g) == 1);
				for(Edge e : g.getEdgesByIndex()) {
					MyAssert.assertTrue(e.orig() != e.dest());
				}
				
				GraphIO.writeJSON(groupname, g);
			}
		}
		System.out.println("done");
	}

}
