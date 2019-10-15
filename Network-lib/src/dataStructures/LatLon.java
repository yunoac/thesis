package dataStructures;

public class LatLon {

	private double lat, lon;
	
	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public double lat() {
		return lat;
	}
	
	public double lon() {
		return lon;
	}
	
	public String toString() {
		return String.format("lat: %.5f, lon: %.5f", lat, lon);
	}
	
}
