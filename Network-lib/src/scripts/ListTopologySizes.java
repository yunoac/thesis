package scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import IO.GraphIO;
import IO.IOTools;
import graph.Graph;
import graph.Graphs;

public class ListTopologySizes {
	
	public static void main(String[] args) {
		ArrayList<File> topologies = IOTools.listTopologies("./data/topologies/");
		for(File f : topologies) {
			Graph g = GraphIO.read(f);
			int nbcc = Graphs.nbConenctedComponents(g);
			System.out.println(g.getName() + "\t" + g.V() + "\t" + g.E() + "\t" + nbcc);
			
		}
	}

}
