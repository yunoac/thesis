package te;

public class Demand {

	private int s, t, v, index, timestamp, duration;
	
	public Demand(int s, int t, int v, int index) {
		this.s = s;
		this.t = t;
		this.v = v;
		this.index = index;
		timestamp = 0;
		duration = 1;
	}
	
	public Demand(int s, int t, int v, int index, int timestamp, int duration) {
		this.s = s;
		this.t = t;
		this.v = v;
		this.index = index;
		this.timestamp = timestamp;
		this.duration = duration;
	}
	
	public void setTimestampDuration(int timestamp, int duration) {
		this.timestamp = timestamp;
		this.duration = duration;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getOrig() {
		return s;
	}
	
	public int getDest() {
		return t;
	}
	
	public int getVol() {
		return v;
	}
	
	public boolean equals(Object other) {
		if(other instanceof Demand) {
			return index == ((Demand)other).index;
		}
		return false;
	}
	
	public int hashCode() {
		return index;
	}
	
	public String toString() {
		return String.format("(%d, %d, %d)", s, t, v);
	}
	
}
