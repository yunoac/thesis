package dataStructures;

import java.util.Iterator;

public class MyBitSet implements Iterable<Integer> {
	
	private long[] b;
	
	public MyBitSet(int maxN) {
		b = new long[maxN / 64 + (maxN % 64 == 1 ? 1 : 0)];
	}
	
	public void set(int i) {
		b[i / 64] |= (1L << (i % 64));
	}
	
	public void clear(int i) {
		b[i / 64]  &= ~(1L << i);
	}
	
	public boolean get(int i) {
		return ((b[i / 64] >> i) & 1) == 1;
	}
	
	public void and(MyBitSet other) {
		for(int i = 0; i < b.length; i++) {
			b[i] &= other.b[i];
		}
	}
	
	public void or(MyBitSet other) {
		for(int i = 0; i < b.length; i++) {
			b[i] |= other.b[i];
		}
	}
	
	public void xor(MyBitSet other) {
		for(int i = 0; i < b.length; i++) {
			b[i] ^= other.b[i];
		}
	}

	public void setMinus(MyBitSet other) {
		for(int i = 0; i < b.length; i++) {
			b[i] = b[i] ^ (b[i] & other.b[i]);
		}
	}

	public Iterator<Integer> iterator() {
		return null;
	}

	private class OneIterator implements Iterator<Integer> {
		
		private int i, j;
		
		public OneIterator() {
			i = 0;
			Integer.
			while(i < b.length && b[i] == 0) i++;
			if(i < b.length) {
				j = Integer.
			}
		}

		public boolean hasNext() {
			return i == b.length;
		}

		public Integer next() {
			return 64 * i + j;
		}
		
	
		
	}
	
}
