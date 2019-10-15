package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import graph.Graph;
import te.Demand;

public class DemandIO {
	
	public static ArrayList<Demand> readDem(Graph g, String path) {
		return readDem(g, new File(path));
	}
	
	public static ArrayList<Demand> readDem(Graph g, File f) {
		try {
			Scanner reader = new Scanner(new FileReader(f));
			ArrayList<Demand> demands = new ArrayList<>();
			int index = 0;
			while(reader.hasNextLine()) {
				String[] data = reader.nextLine().split(" ");
				String orig = data[0];
				String dest = data[1];
				int vol = Integer.parseInt(data[2]);
				Demand demand = new Demand(g.getNodeIndex(orig), g.getNodeIndex(dest), vol, index++);
				demands.add(demand);
			}
			reader.close();
			return demands;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	
	}

	public static ArrayList<Demand> readDemands(String path) {
		return readDemands(new File(path));
	}
	
	public static ArrayList<Demand> readDemands(File f) {
		try {
			Scanner reader = new Scanner(new FileReader(f));
			reader.nextLine();
			reader.nextLine();
			ArrayList<Demand> demands = new ArrayList<>();
			int index = 0;
			while(reader.hasNextLine()) {
				String[] data = reader.nextLine().split(" ");
				int orig = Integer.parseInt(data[1]);
				int dest = Integer.parseInt(data[2]);
				int vol = Integer.parseInt(data[3]);
				Demand demand = new Demand(orig, dest, vol, index++);
				demands.add(demand);
			}
			reader.close();
			return demands;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
