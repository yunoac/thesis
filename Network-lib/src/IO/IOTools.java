package IO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import com.orsoncharts.renderer.category.StandardCategoryColorSource;

public class IOTools {
	
	public static HashSet<String> topologyExtensions = new HashSet<>();
	static {
		//topologyExtensions.add("ntfl");
		//topologyExtensions.add("graph");
		topologyExtensions.add("json");
	}
	
	public static ArrayList<File> listTopologies(String rootDirPath, HashSet<String> ext) {
		File root = new File(rootDirPath);
		ArrayList<File> instances = new ArrayList<>();
		listTopologies(root, instances, ext);
		return instances;
	}
	
	public static ArrayList<File> listTopologies(String rootDirPath) {
		File root = new File(rootDirPath);
		ArrayList<File> instances = new ArrayList<>();
		listTopologies(root, instances);
		return instances;
	}
	
	public static ArrayList<File> listTopologies2(String group) {
		File root = new File("./data/topologies/" + group);
		ArrayList<File> instances = new ArrayList<>();
		listTopologies(root, instances);
		return instances;
	}
	
	
	public static ArrayList<File> listTopologies(File root) {
		ArrayList<File> instances = new ArrayList<>();
		listTopologies(root, instances);
		return instances;
	}
	
	private static void listTopologies(File cur, ArrayList<File> instances) {
		if(cur.isDirectory()) {
			for(File next : cur.listFiles()) {
				listTopologies(next, instances);
			}
		} else {
			String[] tmp = cur.toString().split("[.]");
			String ext = tmp[tmp.length - 1];
			if(topologyExtensions.contains(ext)) {
				instances.add(cur);
			}
		}
	}
	
	private static void listTopologies(File cur, ArrayList<File> instances, HashSet<String> ext) {
		if(cur.isDirectory()) {
			for(File next : cur.listFiles()) {
				listTopologies(next, instances, ext);
			}
		} else {
			String[] tmp = cur.toString().split("[.]");
			String e = tmp[tmp.length - 1];
			if(ext.contains(e)) {
				instances.add(cur);
			}
		}
	}
	
	public static File[] listTopologieGroups() {
		return new File[] {new File("./data/topologies/zoo"), new File("./data/topologies/rf"), new File("./data/topologies/ovh"), new File("./data/topologies/real")};
		
		//return new File[] {new File("./data/topologies/ovh")};
		
		//File f = new File("./data/topologies/");
		//return f.listFiles();
	}

	
}
