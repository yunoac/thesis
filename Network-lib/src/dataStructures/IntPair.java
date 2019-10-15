package dataStructures;

public class IntPair {
	
	private int x, y;
	
	public IntPair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public boolean equals(Object other) {
		if(other instanceof IntPair) {
			IntPair o = (IntPair)other;
			return x == o.x && y == o.y;
		}
		return false;
	}
	
	public int hashCode() {
		return x + 31 * y;
	}

}
