package experiments;

import java.io.File;
import java.util.ArrayList;
import IO.GraphIO;
import IO.IOTools;
import graph.Graph;
import graph.Graphs;
import graph.WeightFunction;
import monitoring.IGPFinder;
import utils.MyAssert;

public class ECMPFree {

	public static void main(String[] args) throws InterruptedException {
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			int k = 0;
			String groupname = group.toString().split("/")[3];
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				System.out.println(f);
				MyAssert.assertTrue(f.getAbsolutePath().endsWith(".json"));
				
				
				Graph g = GraphIO.read(f);
				
				//WeightFunction igpc = IGPFinder.logPrimeCompleteIGP(g);
				WeightFunction igpc = IGPFinder.primeECMPFreeIGP(g).first();
				
				MyAssert.assertTrue(Graphs.isComplete(g, igpc.toArray()));
				
				g.addWeightFunction("igp_complete", igpc);
				
				GraphIO.writeJSON(groupname, g);
				
			}
		}
		System.out.println("done");
	}

}
