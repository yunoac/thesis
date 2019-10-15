package dataStructures;

public class Triple<A, B, C> {

	private A a;
	private B b;
	private C c;
	
	public Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public A first() {
		return a;
	}
	
	public B second() {
		return b;
	}
	
	public C third() {
		return c;
	}
	
	public String toString() {
		return String.format("(%s, %s, %s)", a.toString(), b.toString(), c.toString());
	}
	
}
