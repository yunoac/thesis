package IO;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import graph.Graph;
import sr.SrPath;

public class CycleCoverIO {
	
	public static ArrayList<SrPath> readCover(String jsonPath, String key, Graph g) {
		JSONObject json = JsonIO.read(jsonPath);
		ArrayList<SrPath> cycles = new ArrayList<>();
		
		JSONArray cycleData = json.getJSONArray(key);
		for(int i = 0; i < cycleData.length(); i++) {
			String[] tmp = cycleData.getString(i).split(" ");
			for(int j = 0; j < tmp.length; j++) {
				String s = tmp[j];
				if(s.indexOf('@') != -1) {
					// adjacency segment
					String[] tmp2 = s.split("@");
					
				} else {
					// node segment
				}
			}
		}
	}

}
