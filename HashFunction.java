package nicks_hash_function;

import java.util.BitSet;

public class HashFunction {

	/*
	 * Heavily a work in progress!!
	 * 
	 * note: we are on step 6. I am thinking that I might want to forego the Word
	 * class, and instead just operate on everything using the BitSets. If I do that
	 * then I will need to change all of the operations in the Word class to work
	 * differently. Thats fine though because right now they still all return
	 * BitSets and not Words, so I would have to change it in the future anyways.
	 * The only part I am unsure of is the best way to make them all static methods
	 * and easy to use. So I will need to re-factor and place the operation methods
	 * in the class that I am performing the hashing in.
	 * 
	 * 1. message to binary
	 * 
	 * 2. add a one to the end to signify message end
	 * 
	 * 3. pad with zeros to a multiple 512 bits - 64 bits for the length (Integer)
	 * 
	 * 4. cut into blocks of 512 (message block)
	 * 
	 * 5. cut each block into a message schedule (16 words 32 bits each)
	 * 
	 * 6. propagate words with functions to get the full schedule of 64 words
	 * 
	 * 
	 * 7. compression component of the hash function (state registers- the sqrt of
	 * the first 8 primes)
	 * 
	 * 8. for each word in the message schedule, take it and its corresponding
	 * constant using these two words and the state registers, create the two temp
	 * words
	 * 
	 * 9. compress the temporary words into the register, and move to the next
	 * message word
	 * 
	 * 10. add the current state register to the original state register
	 * 
	 * 11. if there are additional message blocks, use the result as the initial
	 * state register and repeat
	 * 
	 * 12. convert each of the 8 final words to hexadecimal and concatenate them to
	 * get the final hash
	 * 
	 */

	public HashFunction(int index) {

	}

	private static short primes(int index) {
		short[] arr = new short[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79,
				83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191,
				193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307,
				311 };
		return arr[index];
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
		long scale = 1L << 32;
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
		double root = Math.cbrt(primes(index));
		return stringToBits(getRootBinaryString(root));
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
	public static BitSet getRegister() {
		BitSet register = new BitSet(256);
		for (int i = 0; i < 8; i++) {
			String s = getRootBinaryString(Math.sqrt(primes(i)));
			for (int j = 0; j < s.length(); j++) {
				if (s.charAt(s.length() - (1 + j)) == '1') {
					int idx = ((i + 1) * 32) - (1 + j);
					register.set(idx);
				}
			}
		}
		return register;
	}

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

	public void propogateMessage(BitSet message) {

	}

	public static void main(String[] args) {
		String input = "abc";
		BitSet bits = MessageParser.parse(input);
//		printBlock(bits);

//		printBits(primeConstant(0), 32);
//		printBits(getConstant(0), 32);
		BitSet reg = getRegister();
		printWords(reg);
	}

	private static void printBits(BitSet bits, int size) {
		for (int i = 0; i < size; i++) {
			boolean val = bits.get(i);
			System.out.print(val ? 1 : 0);
		}
		System.out.print("\n");
	}

	private static void printWords(BitSet bits) {
		int words = Math.ceilDiv(bits.length(), 32);
		for (int i = 0; i < words; i++) {
			int idx = i * 32;
			printBits(bits.get(idx, idx + 32), 32);
		}
		System.out.print("\n");
	}
}
