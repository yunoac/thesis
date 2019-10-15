package experiments.te;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import IO.DemandIO;
import IO.GraphIO;
import IO.IOTools;
import dataStructures.Edge;
import dataStructures.Pair;
import experiments.RunConfig;
import graph.Graph;
import graph.Graphs;
import graph.Path;
import gurobi.GRB;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBExpr;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import gurobi.GRB.DoubleAttr;
import sr.Segmenter;
import sr.SrPath;
import te.Demand;
import te.MCF;
import te.TEInstance;
import utils.MyAssert;

public class MCFSegCost_ {

	public static String experimentName = "MCFsegcost";

	public static void main(String[] args) {
		
		//cgExample();
		
		// loop over topology group
		for(File group : IOTools.listTopologieGroups()) {
			String groupname = group.toString().split("/")[3];
			if(!groupname.equals("zoo")) continue;
			System.out.println("solving group: " + groupname);
			// loop over topologies in group
			ArrayList<File> topologies = IOTools.listTopologies(group);
			for(File f : topologies) {
				// initialize global graph data
				Graph g = GraphIO.read(f);

				//if(!g.getName().equals("Arpanet19706")) continue;
				File resFile = new File("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".res");
				if(resFile.exists()) {
					System.out.println("skip: " + g.getName());
					continue;
				}


				MyAssert.assertTrue(Graphs.nbSCC(g) == 1);
				
				ArrayList<Demand> demands = DemandIO.readDemands("./data/demands/" + groupname + "/" + g.getName() + ".0000.demands");
				TEInstance instance = new TEInstance(g, demands);
				MCF mcf = new MCF(instance, false);
				mcf.optimize();
				Segmenter seg = new Segmenter(g);
				
				long startTime = System.nanoTime();
				HashMap<Demand, ArrayList<Pair<Path, Double>>> sol = mcf.getPathFlows();
				long endTime = System.nanoTime();
				
				JSONObject data = new JSONObject();
				JSONArray nbSeg = new JSONArray();
				JSONArray nbPaths = new JSONArray();
				
				for(Demand demand : demands) {
					nbPaths.put(sol.get(demand).size());
					for(Pair<Path, Double> p : sol.get(demand)) {
						SrPath ps = seg.segment(p.first());
						nbSeg.put(ps.getSegmentCost());
					}
				}
				
				data.put("nbSeg", nbSeg);
				data.put("nbPaths", nbPaths);
				data.put("runtime", endTime - startTime);
				
				PrintWriter writer;
				File d;
				d = new File("./data");
				if(!d.exists()) d.mkdir();
				d = new File("./data/results");
				if(!d.exists()) d.mkdir();
				d = new File("./data/results/" + groupname);
				if(!d.exists()) d.mkdir();
				d = new File("./data/results/" + groupname + "/" + experimentName);
				if(!d.exists()) d.mkdir();

				try {
					writer = new PrintWriter(new FileWriter("./data/results/" + groupname + "/" + experimentName + "/" + g.getName() + ".res"));
					writer.write(data.toString(4));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			

			}
		}
		System.out.println("DONE");
	}
	
	public static void cgExample() {
		try {
			GRBEnv env = new GRBEnv();
			GRBModel model = new GRBModel(env);
			// initialize the model variables
			GRBVar[] x = new GRBVar[4];
			x[0] = model.addVar(0, GRB.INFINITY, 3, GRB.CONTINUOUS, "x1");
			x[1] = model.addVar(0, GRB.INFINITY,  3, GRB.CONTINUOUS, "x2");
			x[2] = model.addVar(0, GRB.INFINITY, 4, GRB.CONTINUOUS, "x3");
			x[3] = model.addVar(0, GRB.INFINITY, 1, GRB.CONTINUOUS, "x4");
			
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(4, x[0]);
			expr.addTerm(5, x[1]);
			expr.addTerm(3, x[2]);
			expr.addTerm(1.5, x[3]);
			GRBConstr c1 = model.addConstr(expr, GRB.GREATER_EQUAL, 6, "c1");
			
			expr = new GRBLinExpr();
			expr.addTerm(5, x[0]);
			expr.addTerm(6, x[1]);
			expr.addTerm(4, x[2]);
			expr.addTerm(6, x[3]);
		
			GRBConstr c2 = model.addConstr(expr, GRB.GREATER_EQUAL, 7, "c1");
			
			
			
			model.update();
			model.optimize();
			
			
			for(int i = 0; i < x.length; i++) {
				System.out.println(x[i].get(GRB.DoubleAttr.X));
			}
			
			System.out.println();
			
			System.out.println(c1.get(GRB.DoubleAttr.Pi));
			System.out.println(c2.get(GRB.DoubleAttr.Pi));
			
			
			System.exit(0);
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
