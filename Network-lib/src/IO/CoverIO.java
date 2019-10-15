package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import sr.SrPath;

public class CoverIO {
	
	public static void writeCover(ArrayList<SrPath> cover, File file) {
		
	}

	/*
	public static ArrayList<SrPath> readCover(Graph g, File file) {
		try {
			Scanner reader = new Scanner(new FileReader(file));
			String s = new String();
			while(reader.hasNextLine()) {
				s += reader.nextLine();
			}
			reader.close();
			JSONObject obj = new JSONObject(s);
			JSONArray data = obj.getJSONArray("solution");
			for(Object o : data) {
				JSONObject tmp = (JSONObject)o;
				String[] cycleData = tmp.getString("cycle").split(" ");
				SrPath c = new SrPath();
				for(int i = 0; i < cycleData.length; i++) {
					String[] split = cycleData[i].split(",");
					if(split.length == 2 || split.length == 3) {
						// adjacency segment
						
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	*/
	
}
