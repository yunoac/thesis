package experiments;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;

import IO.GraphIO;
import IO.IOTools;
import graph.Graph;
import utils.MyAssert;

public class GeneratorRunner {

	public static void generate(Generator gen) {
		for(File group : IOTools.listTopologieGroups()) {
			String groupname = group.toString().split("/")[3];
			System.out.println("----- RUNNING ON GROUP " + groupname + " -----");
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				MyAssert.assertTrue(f.toString().endsWith(".json"));
				Graph g = GraphIO.read(f);
				if(gen.skip() && gen.expFileExists(groupname, g)) {
					System.out.println("skip: " + g.getName());
					continue;
				}
				System.out.println("[" + groupname + "]" + "generating for: " + g.getName());
				gen.setCurrentGroup(groupname);
				JSONObject res = gen.generate(g);
				if(gen.write()) {
					gen.writeExp(groupname, g, res);					
				}
			}
		}
		System.out.println("DONE");
	}
	
	public static void generate(Generator gen, String group) {
		ArrayList<File> topologies = IOTools.listTopologies2(group);
		for(File f : topologies) {
			MyAssert.assertTrue(f.toString().endsWith(".json"));
			Graph g = GraphIO.read(f);
			if(gen.skip() && gen.expFileExists(group, g)) {
				System.out.println("skip: " + g.getName());
				continue;
			}
			System.out.println("[" + group + "]" + "generating for: " + g.getName());
			gen.setCurrentGroup(group);
			JSONObject res = gen.generate(g);
			if(gen.write()) {
				gen.writeExp(group, g, res);					
			}
		}
		System.out.println("DONE");
	}
	
	public static void generate(Generator gen, String group, String graphname) {
		Graph g = GraphIO.read("./data/topologies/" + group + "/" + graphname + ".json");
		if(gen.skip() && gen.expFileExists(group, g)) {
			System.out.println("skip: " + g.getName());
			return;
		}
		System.out.println("[" + group + "]" + "generating for: " + g.getName());
		gen.setCurrentGroup(group);
		JSONObject res = gen.generate(g);
		if(gen.write()) {
			gen.writeExp(group, g, res);					
		}
		System.out.println("DONE");
	}
	
}
