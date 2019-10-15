package dataStructures;

import java.util.BitSet;
import java.util.Iterator;

public class Bitset extends BitSet implements Iterable<Integer> {

	private static final long serialVersionUID = -4153630096061809585L;

	public Iterator<Integer> iterator() {
		return new BitsetIter(this);
	}
	
	private class BitsetIter implements Iterator<Integer> {

		private BitSet b;
		private int cur;
		
		public BitsetIter(BitSet b) {
			this.b = b;
			cur = b.nextSetBit(0);
		}
		
		@Override
		public boolean hasNext() {
			return cur != -1;
		}

		@Override
		public Integer next() {
			int ret = cur;
			cur = b.nextSetBit(cur + 1);
			return ret;
		}
		
		
	}
	
	public BitsetRevIter getRevIterator() {
		return new BitsetRevIter(this);
	}
	
	private class BitsetRevIter implements Iterator<Integer> {

		private BitSet b;
		private int cur;
		
		public BitsetRevIter(BitSet b) {
			this.b = b;
			cur = b.nextClearBit(0);
		}
		
		@Override
		public boolean hasNext() {
			return cur != -1;
		}

		@Override
		public Integer next() {
			int ret = cur;
			cur = b.nextClearBit(cur + 1);
			return ret;
		}
		
	}


}
