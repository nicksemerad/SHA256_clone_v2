package nicks_hash_function;

import java.util.BitSet;

/**
 * This class contains the methods used to perform operations on BitSets.
 */
public class BitOperations {
	private static Integer[] initialRegister = new Integer[] { 
			0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19 
	};
	private static Integer[] constants = new Integer[] { 
			0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
			0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174, 
			0xe49b69c1, 0xefbe4786, 0xfc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da, 
			0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x6ca6351, 0x14292967, 
			0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 
			0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070, 
			0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 
			0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2 
		};

	/**
	 * Returns the bits of the constant at the provided index. Constants are the
	 * pre-computed cube roots of prime numbers that are operated on to get 32 bits
	 * that appear random, but are not. The constants are used to compress the input
	 * message into the hash register.
	 * 
	 * @param index
	 * @return
	 */
	public static BitSet getConstant(int index) {
		return binaryToBits(Integer.toBinaryString(constants[index]));
	}

	/**
	 * Returns the bits of the initial registry word at the provided index. These
	 * words are the pre-computed square roots of the first 8 prime numbers. They
	 * are operated on to get 32 bits that appear random, but are not.
	 * 
	 * @param index
	 * @return
	 */
	public static BitSet getInitialRegister(int index) {
		return binaryToBits(Integer.toBinaryString(initialRegister[index]));
	}

	/**
	 * Shifts a BitSet right a number of times.
	 * 
	 * @param bits
	 * @param count
	 * @return
	 */
	public static BitSet shr(BitSet bits, int count) {
		BitSet rotated = new BitSet(32);
		for (int i = 0; i < 32 - count; i++) {
			rotated.set(count + i, bits.get(i));
		}
		return rotated;
	}

	/**
	 * Rotates a BitSet right a number of times.
	 * 
	 * @param bits
	 * @param count
	 * @return
	 */
	public static BitSet rotr(BitSet bits, int count) {
		BitSet rotated = bits.get(32 - count, 32);
		for (int i = 0; i < 32 - count; i++) {
			rotated.set(count + i, bits.get(i));
		}
		return rotated;
	}

	/**
	 * Performs an X-OR on two BitSets.
	 * 
	 * @param setOne
	 * @param setTwo
	 * @return
	 */
	public static BitSet xor(BitSet setOne, BitSet setTwo) {
		BitSet clone = (BitSet) setTwo.clone();
		clone.xor(setOne);
		return clone;
	}

	/**
	 * Lower sigma zero.
	 * 
	 * @param bits
	 * @return
	 */
	public static BitSet σ0(BitSet bits) {
		return xor(xor(rotr(bits, 7), rotr(bits, 18)), shr(bits, 3));
	}

	/**
	 * Lower sigma one.
	 * 
	 * @param bits
	 * @return
	 */
	public static BitSet σ1(BitSet bits) {
		return xor(xor(rotr(bits, 17), rotr(bits, 19)), shr(bits, 10));
	}

	/**
	 * Upper sigma zero.
	 * 
	 * @param bits
	 * @return
	 */
	public static BitSet Σ0(BitSet bits) {
		return xor(xor(rotr(bits, 2), rotr(bits, 13)), rotr(bits, 22));
	}

	/**
	 * Upper sigma one.
	 * 
	 * @param bits
	 * @return
	 */
	public static BitSet Σ1(BitSet bits) {
		return xor(xor(rotr(bits, 6), rotr(bits, 11)), rotr(bits, 25));
	}

	/**
	 * Adds two BitSets.
	 * 
	 * @param setOne
	 * @param setTwo
	 * @return
	 */
	public static BitSet add(BitSet setOne, BitSet setTwo) {
		long sum = bitsToLong(setOne) + bitsToLong(setTwo);
		return binaryToBits(Long.toBinaryString(sum % (1L << 32)));
	}

	/**
	 * Adds an array of BitSets.
	 * 
	 * @param sets
	 * @return
	 */
	public static BitSet add(BitSet[] sets) {
		BitSet sum = sets[0];
		for (int i = 1; i < sets.length; i++) {
			sum = add(sum, sets[i]);
		}
		return sum;
	}

	/**
	 * Performs a choice operation using three BitSets. Choice works by first
	 * looking at the value in setOne. If it is 1, the resulting BitSet takes the
	 * value from setTwo. If it is 0, it takes the value from setThree instead.
	 * 
	 * @param setOne
	 * @param setTwo
	 * @param setThree
	 * @return
	 */
	public static BitSet choice(BitSet setOne, BitSet setTwo, BitSet setThree) {
		BitSet bits = new BitSet(32);
		for (int i = 0; i < 32; i++) {
			bits.set(i, setOne.get(i) ? setTwo.get(i) : setThree.get(i));
		}
		return bits;
	}

	/**
	 * Performs a majority operation using three BitSets. The resulting BitSet has
	 * the value that is in the majority of the three sets. If there are two zeros
	 * and a one at a given index, the resulting BitSet will have zero at the same
	 * index.
	 * 
	 * @param setOne
	 * @param setTwo
	 * @param setThree
	 * @return
	 */
	public static BitSet majority(BitSet setOne, BitSet setTwo, BitSet setThree) {
		BitSet bits = new BitSet(32);
		for (int i = 0; i < 32; i++) {
			if (sumBits(setOne.get(i), setTwo.get(i), setThree.get(i))) {
				bits.set(i);
			}
		}
		return bits;
	}
	
	/**
	 * Takes a binary string and converts it into a set of bits. The resulting
	 * BitSet has 32 bits of information making it a word. If the binary string is
	 * shorter than 32 bits, additional zeroes are added at the front of the binary
	 * number to pad it up to 32.
	 * 
	 * @param binaryString
	 * @return
	 */
	public static BitSet binaryToBits(String binaryString) {
		String s = binaryString;
		BitSet bits = new BitSet(32);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(s.length() - (1 + i)) == '1') {
				bits.set(32 - (1 + i));
			}
		}
		return bits;
	}
	
	/**
	 * Converts a BitSet into a long.
	 * 
	 * @param bits
	 * @return
	 */
	public static long bitsToLong(BitSet bits) {
		if (bits == null) {
			return 0L;
		}
		long[] rev = reverse(bits).toLongArray();
		return rev.length > 0 ? rev[0] : 0L;
	}

	/**
	 * Reverses a BitSet.
	 * 
	 * @param set
	 * @return
	 */
	public static BitSet reverse(BitSet set) {
		BitSet reversed = new BitSet(32);
		for (int i = 0; i < 32; i++) {
			reversed.set(i, set.get(31 - i));
		}
		return reversed;
	}

	/**
	 * Finds the sum of three booleans, where 1 == true and 0 == false.
	 * 
	 * @param i
	 * @param setOne
	 * @param setTwo
	 * @param setThree
	 * @return
	 */
	private static boolean sumBits(boolean valOne, boolean valTwo, boolean valThree) {
		return ((valOne ? 1 : 0) + (valTwo ? 1 : 0) + (valThree ? 1 : 0)) > 1;
	}

}
