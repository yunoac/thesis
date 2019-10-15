package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Edge;
import dataStructures.Index;
import dataStructures.LatLon;
import graph.Graph;
import graph.WeightFunction;

/**
 * Class to read graph files.
 *
 * @author f.aubry@uclouvain.be
 */
public class GraphIO {
	
	public static void writeNTFL(File file, Graph g) {
		try {
			String fn = file.getAbsolutePath();
			if(fn.endsWith(".graph")) {
				fn = fn.replace(".graph", ".ntfl");
			}
			file = new File(fn);
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			for(Edge e : g.getEdgesByIndex()) {
				writer.write(String.format("%s %s %.3f %.3f\n", g.getNodeLabel(e.orig()), g.getNodeLabel(e.dest()), g.getWeight("igp", e), g.getWeight("lat", e)));
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void writeJSON(String groupname, Graph g) {
		File file = new File("./data/topologies/" + groupname + "/" + g.getName() + ".json");
		JSONObject data = new JSONObject();
		data.put("V", g.V());
		data.put("E", g.E());
		JSONArray nodes = new JSONArray();
		for(int v = 0; v < g.V(); v++) {
			nodes.put(g.getNodeLabel(v));
		}
		data.put("name", g.getName());
		data.put("nodes", nodes);
		
		JSONArray labels = new JSONArray();
		for(String lbl : g.getWeightLabels()) {
			labels.put(lbl);
		}
		
		data.put("wlbl", labels);
		
		JSONArray edges = new JSONArray();
		for(Edge e : g.getEdgesByIndex()) {
			JSONObject edgeData = new JSONObject();
			edgeData.put("orig", g.getNodeLabel(e.orig()));
			edgeData.put("dest", g.getNodeLabel(e.dest()));
			edgeData.put("index", e.getIndex());
			edgeData.put("reverse", e.getReverse().getIndex());
			for(String lbl : g.getWeightLabels()) {
				edgeData.put(lbl, g.getWeight(lbl, e));
			}
			edges.put(edgeData);
		}
		data.put("edges", edges);
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(file));
			writer.write(data.toString(4));
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public static Graph read(String grouname, String filename) {
		return read("./data/topologies/" + grouname + "/" + filename);
	}
	
    public static Graph read(File file) {
    	if(file.toString().endsWith(".json")) {
    		return readJSON(file);
    	}
    	
    	if(file.toString().endsWith(".ntfl")) {
    		return readNTFL(file);
    	} else if(file.toString().endsWith(".graph")) {
    		return readZoo(file);
    	} else if(file.toString().endsWith(".net")) {
    		return readNet(file);
    	}
    	return null;
    }
   
    public static Graph read(String path) {
    	return read(new File(path));
    }
    
    public static Graph readJSON(File file) {
    	try {
			Scanner reader = new Scanner(new FileReader(file));
			String s = new String();
			while(reader.hasNextLine()) {
				s += reader.nextLine();
			}
			JSONObject obj = new JSONObject(s);
			int V = obj.getInt("V");
			JSONArray nodes = obj.getJSONArray("nodes");
			Index<String> nodeLabels = new Index<>();
			for(Object lbl : nodes) {
				nodeLabels.add((String)lbl);
			}
			Graph g = new Graph(nodeLabels);			
			JSONArray edges = obj.getJSONArray("edges");
		
			HashMap<String, WeightFunction> wfs = new HashMap<>();

			JSONArray lbls = (JSONArray)obj.get("wlbl");
			
			for(Object lblo : lbls) {
				String lbl = (String)lblo;
				wfs.put(lbl, new WeightFunction());
			}
			
			int[] rev = new int[edges.length()];
			
			for(Object tmp : edges) {
				JSONObject edgeData = (JSONObject)tmp;
				int orig = nodeLabels.get(edgeData.getString("orig"));
				int dest = nodeLabels.get(edgeData.getString("dest"));
				int index = edgeData.getInt("index");
				rev[index] = edgeData.getInt("reverse");
				Edge e = new Edge(orig, dest, index);
				
				for(Object lblo : lbls) {
					String lbl = (String)lblo;
					wfs.get(lbl).setWeight(e, edgeData.getDouble(lbl));
				}
				
				g.addEdge(e);
			}
			
			Edge[] E = g.getEdgesByIndex();
			for(int i = 0; i < E.length; i++) {
				E[i].setReverse(E[rev[i]]);
			}
			
			g.setWeightFunctions(wfs);
			
			g.setName(obj.getString("name"));
			
			reader.close();

			return g;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public static Graph readZoo(File file) {
    	try {
			Scanner reader = new Scanner(new FileReader(file));
			int V = Integer.parseInt(reader.nextLine().split(" ")[1]);
			reader.nextLine();
			LatLon[] positions = new LatLon[V];
			Index<String> nodeLabels = new Index<>();
			for(int i = 0; i < V; i++) {
				String[] data = reader.nextLine().split(" ");
				String label = data[0];
				nodeLabels.add(label);
				double lat = Double.parseDouble(data[1]);
				double lon = Double.parseDouble(data[2]);
				positions[i] = new LatLon(lat, lon);
			}
			Graph g = new Graph(nodeLabels, positions);
			reader.nextLine();
			int E = Integer.parseInt(reader.nextLine().split(" ")[1]);
			reader.nextLine();
			WeightFunction IGP = new WeightFunction();
			WeightFunction LAT = new WeightFunction();
			WeightFunction BDW = new WeightFunction();
			for(int i = 0; i < E; i++) {
				String[] data = reader.nextLine().split(" ");
				String label = data[0];
				int orig = Integer.parseInt(data[1]);
				int dest = Integer.parseInt(data[2]);
				int igp = Integer.parseInt(data[3]);
				int bdw = Integer.parseInt(data[4]);
				int lat = Integer.parseInt(data[5]);
				Edge e = new Edge(orig, dest, i, label);
				g.addEdge(e);
				e.setGraph(g);
				IGP.setWeight(e, igp);
				BDW.setWeight(e, bdw);
				LAT.setWeight(e, lat);
			}
			g.addWeightFunction("igp", IGP);
			g.addWeightFunction("lat", LAT);
			g.addWeightFunction("bdw", BDW);
			String[] tmp = file.toString().split("/");
			tmp = tmp[tmp.length - 1].split("[.]");
			g.setName(tmp[0]);
			reader.close();
			return g;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    private static Graph readNet(File file) {
    	Scanner reader;
		try {
			reader = new Scanner(new FileReader(file));
			// build an index with the node labels
			Index<String> nodeLabels = new Index<>();
			while(reader.hasNextLine()) {
	    		String line = reader.nextLine();
	    		String[] data = line.split("[ ]+");
	    		String orig = data[0];
	    		String dest = data[1];
	    		nodeLabels.add(orig);
	    		nodeLabels.add(dest);
	    	}
			// build the graph
			reader = new Scanner(new FileReader(file));
			Graph g = new Graph(nodeLabels);
			g.createWeightFuntion("igp");
			g.createWeightFuntion("lat");
			g.createWeightFuntion("bdw");
			int index = 0;
			while(reader.hasNextLine()) {
				String line = reader.nextLine();
	    		String[] data = line.split("[ ]+");
	    		String orig = data[0];
	    		String dest = data[1];
	    		Edge edge = new Edge(nodeLabels.get(orig), nodeLabels.get(dest), index++);
	    		edge.setGraph(g);
	    		g.addEdge(edge);
	    		g.setWeight("igp", edge, Double.parseDouble(data[2]));
	    		g.setWeight("lat", edge, Double.parseDouble(data[3]));
	    		g.setWeight("bdw", edge, Double.parseDouble(data[4]));
	    		// create other edge
	    		edge = new Edge(nodeLabels.get(dest), nodeLabels.get(orig), index++);
	    		edge.setGraph(g);
	    		g.addEdge(edge);
	    		g.setWeight("igp", edge, Double.parseDouble(data[2]));
	    		g.setWeight("lat", edge, Double.parseDouble(data[3]));
	    		g.setWeight("bdw", edge, Double.parseDouble(data[4]));
	    	}
			String[] tmp = file.toString().split("/");
			tmp = tmp[tmp.length - 1].split("[.]");
			g.setName(tmp[0]);
			reader.close();
			return g;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
	
    }
    
    private static Graph readNTFL(File file) {
    	Scanner reader;
		try {
			reader = new Scanner(new FileReader(file));
			// build an index with the node labels
			Index<String> nodeLabels = new Index<>();
			while(reader.hasNextLine()) {
	    		String line = reader.nextLine();
	    		String[] data = line.split("[ ]+");
	    		String orig = data[0];
	    		String dest = data[1];
	    		nodeLabels.add(orig);
	    		nodeLabels.add(dest);
	    	}
			// build the graph
			reader = new Scanner(new FileReader(file));
			Graph g = new Graph(nodeLabels);
			g.createWeightFuntion("igp");
			g.createWeightFuntion("lat");
			g.createWeightFuntion("bdw");
			int index = 0;
			while(reader.hasNextLine()) {
				String line = reader.nextLine();
	    		String[] data = line.split("[ ]+");
	    		String orig = data[0];
	    		String dest = data[1];
	    		Edge edge = new Edge(nodeLabels.get(orig), nodeLabels.get(dest), index++);
	    		edge.setGraph(g);
	    		g.addEdge(edge);
	    		g.setWeight("igp", edge, Double.parseDouble(data[2]));
	    		g.setWeight("lat", edge, Double.parseDouble(data[3]));
	    		g.setWeight("bdw", edge, 1);
	    	}
			String[] tmp = file.toString().split("/");
			tmp = tmp[tmp.length - 1].split("[.]");
			g.setName(tmp[0]);
			reader.close();
			return g;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return null;
    }

   
}