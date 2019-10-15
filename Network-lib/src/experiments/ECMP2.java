package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import dataStructures.Pair;
import graph.ECMP;
import graph.Graph;
import sr.SrReach;

public class ECMP2 {
	
	static String experimentName = "ecmp2";

	public static void main(String[] args) throws InterruptedException {
		// loop over topology group
		int total = 0;
		int free = 0;
		for(File group : IOTools.listTopologieGroups()) {
			String groupname = group.toString().split("/")[3];
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				// initialize global graph data
				Graph g = GraphIO.read(f);
				
				ECMP ecmp = new ECMP(g);
				
				if(ecmp.ecmpFree()) {
					free += 1;
				}
				total += 1;
				System.out.println(ecmp.ecmpFree());
				/*
				JSONObject data = new JSONObject();
				data.put("kmin", p.first());
				data.put("kmax", p.second());
				
				// write the results
				PrintWriter writer;
				File d;
				d = new File("./data");
				if(!d.exists()) d.mkdir();
				d = new File("./data/results");
				if(!d.exists()) d.mkdir();
				d = new File("./data/results/" + groupname);
				if(!d.exists()) d.mkdir();
				d = new File("./data/results/" + groupname + "/" + experimentName);
				if(!d.exists()) d.mkdir();

				try {
					writer = new PrintWriter(new FileWriter("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".res"));
					writer.write(data.toString(4));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				*/
			}
		}
		System.out.println((double)free / total);
		System.out.println("done");
	}


}
