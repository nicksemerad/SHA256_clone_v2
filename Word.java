package nicks_hash_function;

import java.util.BitSet;

/**
 * 
 */
public class Word {
	private BitSet bits;

	/**
	 * 
	 * @param bits
	 */
	public Word(BitSet bits) {
		this.bits = bits;
	}
	
	/**
	 * 
	 * @param longValue
	 */
	public Word(long longValue) {
		this(longToBits(longValue));
	}
	
	/**
	 * 
	 * @param count
	 * @return
	 */
	public BitSet shr(int count) {
		BitSet rotated = new BitSet(32);
		for (int i = 0; i < 32 - count; i++) {
			rotated.set(count + i, bits.get(i));
		}
		return rotated;
	}
	
	/**
	 * 
	 * @param count
	 * @return
	 */
	public BitSet rotr(int count) {
		BitSet rotated = bits.get(32 - count, 32);
		for (int i = 0; i < 32 - count; i++) {
			rotated.set(count + i, bits.get(i));
		}
		return rotated;
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet xor(BitSet other) {
		BitSet clone = (BitSet) other.clone();
		clone.xor(bits);			
		return clone;
	}
	
	public BitSet xor(BitSet setOne, BitSet setTwo) {
		BitSet clone = (BitSet) setTwo.clone();
		clone.xor(setOne);			
		return clone;
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet add(BitSet other) {
		long scale = 1L << 32;
		long sum = getBitsValue(bits) + getBitsValue(other);
		return longToBits(sum % scale);
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet σ0(BitSet other) {
		return xor(xor(rotr(7), rotr(18)), shr(3));
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet σ1(BitSet other) {
		return xor(xor(rotr(17), rotr(19)), shr(10));
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet Σ0(BitSet other) {
		return xor(xor(rotr(2), rotr(13)), rotr(22));
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet Σ1(BitSet other) {
		return xor(xor(rotr(6), rotr(11)), shr(25));
	}
	
	/* 
	 * choice: for three words, if word1[0] is 1 take word2[0], otherwise take word3[0] 
	 * 
	 * majority: for three words take the majority at each index
	 */
	public BitSet choice(BitSet setOne, BitSet setTwo, BitSet setThree) {
		BitSet bits = new BitSet(32);
		for(int i = 0; i < 32; i++) {
			bits.set(i, setOne.get(i) ? setTwo.get(i) : setThree.get(i));
		}
		return bits;
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	public BitSet majority(BitSet setOne, BitSet setTwo, BitSet setThree) {
		BitSet bits = new BitSet(32);
		for(int i = 0; i < 32; i++) {
			bits.set(i, sumBits(i, setOne, setTwo, setThree));
		}
		return bits;
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	private boolean sumBits(int i, BitSet setOne, BitSet setTwo, BitSet setThree) {
		return ((setOne.get(i) ? 1 : 0) + (setTwo.get(i) ? 1 : 0) + (setThree.get(i) ? 1 : 0)) > 1;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BitSet longToBits(long value) {
		String bitString = Long.toBinaryString(value);
		int numBits = bitString.length();
		BitSet bits = new BitSet(32);
		for (int i = 0; i < numBits; i++) {
			if (bitString.charAt(numBits - (1 + i)) == '1') {
				bits.set(31 - i);
			}
		}
		return bits;
	}
	
	/**
	 * 
	 * @param set
	 * @return
	 */
	public static long getBitsValue(BitSet set) {
		return reverse(set).toLongArray()[0];
	}
	
	/**
	 * 
	 * @param set
	 * @return
	 */
	public static BitSet reverse(BitSet set) {
		BitSet reversed = new BitSet(32);
		for(int i = 0; i < 32; i++) {
			reversed.set(i, set.get(31-i));
		}
		return reversed;
	}
	
	/**
	 * 
	 */
	public void print() {
		for (int i = 0; i < 32; i++) {
			boolean val = bits.get(i);
			System.out.print(val ? 1 : 0);
		}
		System.out.print("\n");
	}
}
