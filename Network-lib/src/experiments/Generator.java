package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONObject;

import graph.Graph;

public abstract class Generator {
	
	private String name, group;
	private boolean skip, write;
	
	public Generator(String name, boolean skip, boolean write) {
		this.name = name;
		this.skip = skip;
		this.write = write;
	}
	
	public void setCurrentGroup(String group) {
		this.group = group;
	}
	
	public String getCurrentGroup() {
		return group;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean skip() {
		return skip;
	}

	public boolean write() {
		return write;
	}
	
	public boolean expFileExists(String groupname, Graph g) {
		File resfile = new File("./data/" + getName() + "/" + groupname + "/" + g.getName() + ".json");
		return resfile.exists();
	}
	
	public void writeExp(String groupname, Graph g, JSONObject result) {
		File d;
		d = new File("./data");
		if(!d.exists()) d.mkdir();
		d = new File("./data/" + getName());
		if(!d.exists()) d.mkdir();
		d = new File("./data/" + getName() + "/" + groupname);
		if(!d.exists()) d.mkdir();
		File resFile = new File("./data/" + getName() + "/" + groupname + "/" + g.getName() + ".json");
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(resFile));
			writer.write(result.toString(4));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract JSONObject generate(Graph g);
	
}
