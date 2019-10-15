package dataStructures;

public class Pair<A, B> {

	private A a;
	private B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A first() {
		return a;
	}
	
	public B second() {
		return b;
	}
	
	public String toString() {
		return String.format("(%s, %s)", a.toString(), b.toString());
	}
	
}
