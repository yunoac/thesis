package disjointPaths;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import dataStructures.Edge;
import dataStructures.RDPDemand;
import graph.BiconnectedComponents;
import graph.Graph;
import utils.MyAssert;

public class GenerateST {
	
	/*
	 * Generates tuples (s1, t1, s2, t2) such that:
	 * 
	 * 1. (s1, s2) in E(G)
	 * 2. (t1, t2) in E(G)
	 * 3. |{s1, t1, s2, t2}| = 4
	 * 4. s1 t1 are in the same biconnected component
	 * 5. s2 t2 are in the same biconnected component
	 */
	public static ArrayList<RDPDemand> generateSSTTBiconnected(Graph g, int n) {
		Random rnd = new Random(31);
		BiconnectedComponents bcc = new BiconnectedComponents(g);
		ArrayList<RDPDemand> demands = new ArrayList<>();
 		for(int i = 0; i < n; i++) {
			int s1 = -1, s2 = -1, t1 = -1, t2 = -1;
			while(true) {
				s1 = rnd.nextInt(g.V());
				LinkedList<Edge> out = g.outEdges(s1);
				s2 = out.get(rnd.nextInt(out.size())).dest();
				while(true) {
					t1 = rnd.nextInt(g.V());
					while(t1 == s1 || t1 == s2) {
						t1 = rnd.nextInt(g.V());
					}
					out = g.outEdges(t1);
					t2 = out.get(rnd.nextInt(out.size())).dest();
					if(t2 != s1 && t2 != s2) break;
				}
				if(bcc.sameBcc(s1, t1) && bcc.sameBcc(s2, t2)) break;
			}
			MyAssert.assertTrue(s1 != -1 && s2 != -1 && t1 != -1 && t2 != -1 && s1 != s2 && s1 != t1 && s1 != t2 && s2 != t1 && s2 != t2 && t1 != t2);
			RDPDemand demand = new RDPDemand(s1, t1, s2, t2);
			demands.add(demand);
 		}
 		return demands;
	}
	
	public static JSONObject demandsToJSON(Graph g, LinkedList<RDPDemand> demands) {
		JSONArray st = new JSONArray();
		for(RDPDemand demand : demands) {
			JSONObject data = new JSONObject();
			data.put("s1", g.getNodeLabel(demand.s1()));
			data.put("s2", g.getNodeLabel(demand.s2()));
			data.put("t1", g.getNodeLabel(demand.t1()));
			data.put("t2", g.getNodeLabel(demand.t2()));
			st.put(data);
		}
		JSONObject res = new JSONObject();
		res.put("pairs", st);
		return res;
	}

}
