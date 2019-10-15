package dataStructures;

public class OrderablePair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<OrderablePair<A, B>> {

	private A a;
	private B b;
	
	public OrderablePair(A a, B b) {
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

	public int compareTo(OrderablePair<A, B> other) {
		int acmp = a.compareTo(other.a);
		if(acmp == 0) return b.compareTo(other.b);
		return acmp;
	}
	
}
