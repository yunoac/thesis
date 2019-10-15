package IO;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.RDPDemand;
import graph.Graph;

public class STIO {

	public static LinkedList<RDPDemand> readST(String name, String group, Graph g) {
		JSONObject data = JsonIO.read("./data/" + name + "/" + group + "/" + g.getName() + ".json");
		JSONArray a = data.getJSONArray("pairs");
		LinkedList<RDPDemand> st = new LinkedList<>();
		for(Object o : a) {
			JSONObject tmp = (JSONObject)o;
			st.add(new RDPDemand(g.getNodeIndex(tmp.getString("s1")), 
					             g.getNodeIndex(tmp.getString("t1")), 
					             g.getNodeIndex(tmp.getString("s2")), 
					             g.getNodeIndex(tmp.getString("t2"))));
		}
		return st;
	}
	
}
