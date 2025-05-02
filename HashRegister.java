package nicks_hash_function;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 */
public class HashRegister {
	private static long scale;
	private static short[] primes;
	private LinkedList<BitSet> tempRegister;

	public HashRegister(String input) {
		scale = 1L << 32;
		primes = new short[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
				89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193,
				197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311 };
		initRegister();
		compressBlocks(MessageParser.parse(input));
		HashFunction.printList(tempRegister);
	}

	/**
	 * Calculates and returns the hash register, which is what the entire message is
	 * compressed into. This method only gets the initial register before any
	 * compression has happened. The initial register has 8 words of 32 bits each.
	 * Each word is the square root of the first 8 prime numbers, once they have
	 * been operated on to get the 32 bits of information.
	 * 
	 * @return
	 */
	public void initRegister() {
		tempRegister = new LinkedList<BitSet>();
		for (int i = 0; i < 8; i++) {
			String s = getRootBinaryString(Math.sqrt(primes[i]));
			BitSet bs = stringToBits(s);
			tempRegister.addLast(bs);
		}
	}

	/**
	 * Compresses each of the message blocks into the register.
	 * 
	 * @param message
	 */
	public void compressBlocks(BitSet message) {
		int numBlocks = MessageParser.messageSize(message.length()) / 512;
		for(int i = 0; i < numBlocks; i++) {
			Block block = new Block(message.get(i * 512, (i + 1) * 512));
			block.propogate();
			compress(block);
		}
	}
	
	/**
	 * Compresses a given block into the register.
	 * 
	 * @param block
	 */
	public void compress(Block block) {
		for(int i = 0; i < 64; i++) {
			updateRegister(block, getConstant(i), block.words[i]);
		}
	}
	
	/**
	 * 
	 * @param block
	 * @param indexWords
	 */
	public void updateRegister(Block block, BitSet constant, BitSet schedule) {
		Iterator<BitSet> itr = tempRegister.iterator();
		BitSet[] r = new BitSet[8];
		for(int i = 0; i < 8; i++) {
			r[i] = itr.next();
		}
		BitSet T1 = add(Σ1(r[4]), choice(r[4], r[5], r[6]));
		T1 = add(T1, r[7]);
		T1 = add(T1, schedule);
		T1 = add(T1, constant);
		BitSet T2 = add(Σ0(r[0]), majority(r[0], r[1], r[2]));
		

		tempRegister.addFirst(add(T1, T2));
		tempRegister.set(4, add(tempRegister.get(4), T1));
		tempRegister.removeLast();
	}

	/**
	 * Converts a root as a double value into a binary string. First the root value
	 * modulus 1 is taken. This is done to isolate the fraction portion of the root.
	 * The fraction is then multiplied by the scale which is 2^32. This gives us an
	 * integer that is equal to or less than 32 bits of information.
	 * 
	 * @param root
	 * @return
	 */
	public static String getRootBinaryString(double root) {
		root = (root % 1) * scale;
		return Long.toBinaryString((long) root);
	}

	/**
	 * Returns the bits of the constant at the provided index. Constants are the
	 * cube roots of prime numbers that are operated on to get 32 bits that appear
	 * random, but are not. The constants are used to compress the input message
	 * into the hash register.
	 * 
	 * @param index
	 * @return
	 */
	public static BitSet getConstant(int index) {
		double root = Math.cbrt(primes[index]);
		return stringToBits(getRootBinaryString(root));
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
	public static BitSet stringToBits(String binaryString) {
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
		return stringToBits(Long.toBinaryString(sum % scale));
	}
	
	/**
	 * Adds an array of BitSets.
	 * 
	 * @param setOne
	 * @param setTwo
	 * @return
	 */
	public static BitSet add(BitSet[] sets) {
		long sum = 0L;
		for(int i = 0; i < sets.length; i++) {
			sum+=bitsToLong(sets[i]);
		}
		return stringToBits(Long.toBinaryString(sum % scale));
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

	/**
	 * 
	 */
	private class Block {
		public BitSet[] words;

		/**
		 * 
		 * @param blockBits
		 */
		public Block(BitSet blockBits) {
			words = new BitSet[64];
			for (int i = 0; i < 16; i++) {
				words[i] = blockBits.get((i * 32), ((i + 1) * 32));
			}
		}

		/**
		 * Propagates a message block from 16 words (512 bits) to 64 words (2048 bits)
		 * 
		 * @param m
		 */
		public void propogate() {
			for (int i = 16; i < 64; i++) {
				BitSet word0 = words[i - 16];
				BitSet word1 = σ0(words[i - 15]);
				BitSet word9 = words[i - 7];
				BitSet word14 = σ1(words[i - 2]);
				words[i] = add(new BitSet[] {word0, word1, word9, word14});
			}
//			HashFunction.printBits(words[63], 32);
		}
	}
}
