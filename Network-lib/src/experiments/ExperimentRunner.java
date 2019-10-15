package experiments;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import graph.Graph;
import utils.MyAssert;

public class ExperimentRunner {

	public static void run(Experiment exp) {
		for(File group : IOTools.listTopologieGroups()) {
			String groupname = group.toString().split("/")[3];
			System.out.println("----- RUNNING ON GROUP " + groupname + " -----");
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				MyAssert.assertTrue(f.toString().endsWith(".json"));
				Graph g = GraphIO.read(f);
				if(exp.skip() && exp.expFileExists(groupname, g)) {
					System.out.println("skip: " + g.getName());
					continue;
				}
				System.out.println("[" + groupname + "]" + "running on: " + g.getName());
				exp.setCurrentGroup(groupname);
				JSONObject res = exp.run(g);
				if(exp.write()) {
					System.out.println("writting");
					exp.writeExp(groupname, g, res);					
				}
			}
		}
		System.out.println("DONE");
	}
	
	public static void run(Experiment exp, int minNodes, int maxNodes) {
		for(File group : IOTools.listTopologieGroups()) {
			String groupname = group.toString().split("/")[3];
			System.out.println("----- RUNNING ON GROUP " + groupname + " -----");
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				MyAssert.assertTrue(f.toString().endsWith(".json"));
				Graph g = GraphIO.read(f);
				if(g.V() < minNodes || g.V() > maxNodes) continue;
				if(exp.skip() && exp.expFileExists(groupname, g)) {
					System.out.println("skip: " + g.getName());
					continue;
				}
				System.out.println("[" + groupname + "]" + "running on: " + g.getName());
				exp.setCurrentGroup(groupname);
				JSONObject res = exp.run(g);
				if(exp.write()) {
					exp.writeExp(groupname, g, res);					
				}
			}
		}
		System.out.println("DONE");
	}
	
	public static void run(Experiment exp, String group) {
		ArrayList<File> topologies = IOTools.listTopologies2(group);
		for(File f : topologies) {
			MyAssert.assertTrue(f.toString().endsWith(".json"));
			Graph g = GraphIO.read(f);
			if(exp.skip() && exp.expFileExists(group, g)) {
				System.out.println("skip: " + g.getName());
				continue;
			}
			System.out.println("[" + group + "]" + "running on: " + g.getName());
			exp.setCurrentGroup(group);
			JSONObject res = exp.run(g);
			if(exp.write()) {
				exp.writeExp(group, g, res);					
			}
		}
		System.out.println("DONE");
	}
	
	public static void run(Experiment exp, String group, String graphname) {
		Graph g = GraphIO.read("./data/topologies/" + group + "/" + graphname + ".json");
		if(exp.skip() && exp.expFileExists(group, g)) {
			System.out.println("skip: " + g.getName());
			return;
		}
		System.out.println("[" + group + "]" + "running on: " + g.getName());
		exp.setCurrentGroup(group);
		JSONObject res = exp.run(g);
		if(exp.write()) {
			exp.writeExp(group, g, res);					
		}
		System.out.println("DONE");
	}
	
	public static void run2(Experiment exp, String group, String graphfn) {
		Graph g = GraphIO.read("./data/topologies/" + group + "/" + graphfn);
		if(exp.skip() && exp.expFileExists(group, g)) {
			System.out.println("skip: " + g.getName());
			return;
		}
		System.out.println("[" + group + "]" + "running on: " + g.getName());
		exp.setCurrentGroup(group);
		JSONObject res = exp.run(g);
		if(exp.write()) {
			exp.writeExp(group, g, res);					
		}
		System.out.println("DONE");
	}
	
}
