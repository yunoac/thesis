package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.json.JSONObject;

public class JsonIO {
	
	public static JSONObject read(String path) {
		return read(new File(path));
	}
	
	public static JSONObject read(File f) {
		try {
			Scanner reader = new Scanner(new FileReader(f));
			StringBuilder sb = new StringBuilder();
			while(reader.hasNextLine()) {
				sb.append(reader.nextLine());
			}
			reader.close();
	        JSONObject object = new JSONObject(sb.toString());
	        return object;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject read(String groupname, String graphname, String experiment) {
		return read("./data/results/" + groupname + "/" + experiment + "/" + graphname + ".res");
	}

}
