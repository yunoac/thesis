package experiments.te;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONObject;

import dataStructures.Edge;
import experiments.Experiment;
import graph.Graph;
import sr.ForwGraphs;
import sr.SrOptim;
import sr.SrPath;
import te.Demand;
import utils.ArrayExt;

public class DynamicMaxCapTE extends Experiment {
	

	private ArrayList<Demand> demands;
	private HashMap<Integer, ArrayList<Event>> eventsByTime;
	private ArrayList<Integer> timestamps;
	private int maxSeg;
	
	public DynamicMaxCapTE(String name, boolean skip, boolean write, ArrayList<Demand> demands, int maxSeg) {
		super(name, skip, write);
		this.demands = demands;
		this.maxSeg = maxSeg;
		buildEvents();
	}

	public JSONObject run(Graph g) {
		// initialize forwarding graphs
		System.out.println("initializeing forwarding graps");
		ForwGraphs forw = new ForwGraphs(g);
		// initialize edge capacities
		System.out.println("initializing loads");
		double[] volume = new double[g.E()];
		for(Edge e : g.getEdgesByIndex()) {
			volume[e.getIndex()] = 0;
		}
		// initialize path map
		HashMap<Demand, SrPath> paths = new HashMap<>();
		// simulate
		System.out.println("simulating");
		JSONObject result = new JSONObject();
		for(int timestamp : timestamps) {
			System.out.println("running timestamp: " + timestamp);
			ArrayList<Event> end = new ArrayList<>();
			ArrayList<Event> start = new ArrayList<>();
			for(Event event : eventsByTime.get(timestamp)) {
				if(event.isStart()) start.add(event);
				else end.add(event);
			}
			System.out.println("removing demands");
			for(Event event : end) {
				SrPath p = paths.remove(event.demand);
				for(Edge e : p.getEdges(g, forw)) {
					volume[e.getIndex()] -= event.demand.getVol();
				}
			}
			System.out.println("adding demands");
			for(Event event : start) {
				// compute path for demand
				SrPath p = SrOptim.maxCapSrPath(g, forw, orig, dest, timestamp);
				paths.put(event.demand, p);
				for(Edge e : p.getEdges(g, forw)) {
					volume[e.getIndex()] -= event.demand.getVol();
				}
			}
			System.out.println("computing max congestion");
			double maxRatio = Double.NEGATIVE_INFINITY;
			for(Edge e : g.getEdgesByIndex()) {
				maxRatio = Math.max(maxRatio, volume[e.getIndex()] / g.getWeight("bnd", e));
			}
			System.out.println("max ratio: " + (int)(100 * maxRatio) + "%");
			JSONObject res = new JSONObject();
			res.put("maxRatio", maxRatio);
			res.put("totalVol", ArrayExt.sum(volume));
			result.put(timestamp + "", res);
		}
		return result;
	}
	
	private void buildEvents() {
		eventsByTime = new HashMap<>();
		for(int i = 0; i < demands.size(); i++) {
			Demand demand = demands.get(i);
			Event start = new Event(demand, demand.getTimestamp(), 2 * i);
			Event end = new Event(demand, demand.getTimestamp(), 2 * i + 1);
			if(!eventsByTime.containsKey(start.timestamp)) {
				eventsByTime.put(start.timestamp, new ArrayList<>());
			}
			if(!eventsByTime.containsKey(end.timestamp)) {
				eventsByTime.put(end.timestamp, new ArrayList<>());
			}
			eventsByTime.get(start.timestamp).add(start);
			eventsByTime.get(end.timestamp).add(end);
		}
		timestamps = new ArrayList<>();
		for(int timestamp : eventsByTime.keySet()) {
			Collections.sort(eventsByTime.get(timestamp));
			timestamps.add(timestamp);
		}
		Collections.sort(timestamps);
	}
	
	public class Event implements Comparable<Event> {
		
		private Demand demand;
		private int timestamp, id;
		
		public Event(Demand demand, int timestamp, int id) {
			this.demand = demand;
			this.timestamp = timestamp;
			this.id = id;
		}
		
		public boolean isStart() {
			return this.timestamp == demand.getTimestamp();
		}

		public int compareTo(Event o) {
			int dt = timestamp - o.timestamp;
			if(dt == 0) {
				if(isStart() && o.isStart()) return id - o.id;
				if(isStart()) return -1;
				if(o.isStart()) return 1;
			}
			return dt;
		}
		
		public String toString() {
			if(isStart()) return String.format("[%d]start:%s", timestamp, demand);
			return String.format("[%d]end:%s", timestamp, demand);
		}
		
	}

}
