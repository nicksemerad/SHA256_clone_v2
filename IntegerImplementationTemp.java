package nicks_hash_function;

/**
 * Temporary working space while I figure out how this will work using an
 * integer implementation.
 */
public class IntegerImplementationTemp {
	private static int[] initialRegister = new int[] { 0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
			0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19 };
	private static int[] constants = new int[] { 0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b,
			0x59f111f1, 0x923f82a4, 0xab1c5ed5, 0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe,
			0x9bdc06a7, 0xc19bf174, 0xe49b69c1, 0xefbe4786, 0xfc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc,
			0x76f988da, 0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x6ca6351, 0x14292967,
			0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 0xa2bfe8a1,
			0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070, 0x19a4c116, 0x1e376c08,
			0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, 0x84c87814,
			0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2 };

	public static String print(int integer) {
		StringBuilder finalInt = new StringBuilder();
		String intBinary = Integer.toBinaryString(integer);
		for (int k = 0; k < 32 - intBinary.length(); k++) {
			finalInt.append("0");
		}
		finalInt.append(intBinary);
		return finalInt.toString();
	}

	/**
	 * Prints all the words in my new implementation message next to the old to
	 * compare and see if things are working correctly.
	 * 
	 * @param message
	 * @param message2
	 */
	public static void print(int[][] message, Message message2) {
		for (int i = 0; i < message.length; i++) {
			for (int j = 0; j < 64; j++) {
				System.out.println(print(message[i][j]));
				HashFunction.print(message2.getBlock(i)[j]);
				System.out.println();
			}
		}

	}

	/**
	 * Prints the entire message made with the new implementation.
	 * 
	 * @param message
	 */
	public static void print(int[][] message) {
		for (int i = 0; i < message.length; i++) {
			for (int j = 0; j < 64; j++) {
				System.out.println(Integer.toBinaryString(message[i][j]));
			}
			System.out.println();
		}

	}

	/**
	 * Returns the integer at an index in the string, if it is not out of bounds. If
	 * it is, return 0.
	 * 
	 * @param s
	 * @param index
	 * @return
	 */
	public static char intVal(String s, int index) {
		return index < s.length() ? s.charAt(index) : 0;
	}

	/**
	 * Builds a message based on the input string.
	 * 
	 * @param s
	 * @return
	 */
	public static int[][] buildMessage(String s) {
		s += (char) 128;
		int words = s.length() / 4 + 2;
		int blocks = words / 16 + 1;
		int[][] message = new int[blocks][];

		for (int i = 0; i < blocks; i++) {
			message[i] = new int[64];
			for (int j = 0; j < 16; j++) {
				int idx = i * 64 + j * 4;
				message[i][j] = (intVal(s, idx) << 24) + (intVal(s, idx + 1) << 16) + (intVal(s, idx + 2) << 8)
						+ (intVal(s, idx + 3) << 0);
			}
		}
		long bits = (s.length() - 1) * 8;
		long bitsHigh = bits >>> 32;
		long bitsLow = bits % (2L << 32);

		message[blocks - 1][14] = (int) bitsHigh;
		message[blocks - 1][15] = (int) bitsLow;
		propogate(message);
		return message;
	}

	/**
	 * Propagates each message block from 16 words/ integers to 64.
	 * 
	 * @param message
	 */
	public static void propogate(int[][] message) {
		for (int i = 0; i < message.length; i++) {
			for (int j = 16; j < 64; j++) {
				message[i][j] = message[i][j - 16] + σ0(message[i][j - 15]) + message[i][j - 7] + σ1(message[i][j - 2]);
			}
		}
	}

	/**
	 * Performs a right rotation.
	 * 
	 * @param word
	 * @param rotations
	 * @return
	 */
	public static int rotr(int word, int rotations) {
		int p1 = word >>> rotations;
		int p2 = word << (32 - rotations);
		return p1 + p2;
	}

	/**
	 * Lower case sigma zero operation.
	 * 
	 * @param word
	 * @return
	 */
	public static int σ0(int word) {
		return rotr(word, 7) ^ rotr(word, 18) ^ (word >>> 3);
	}

	/**
	 * Lower case sigma one operation.
	 * 
	 * @param word
	 * @return
	 */
	public static int σ1(int word) {
		return rotr(word, 17) ^ rotr(word, 19) ^ (word >>> 10);
	}

	/**
	 * Performs a choice operation on three integer words. Using the bits in the
	 * first word, the result word is decided. If word1[index] is 1, the bit at
	 * word2[index] is taken. If it is 0, word3[index] is taken instead.
	 * 
	 * @param word1
	 * @param word2
	 * @param word3
	 * @return
	 */
	public static int choice(int word1, int word2, int word3) {
		return (word1 & word2) ^ (~word1 & word3);
	}

	/**
	 * Performs a majority operation on three integer words. The bits in each word
	 * are taken, and the majority at each index is the result bit in the output
	 * word. If word1[index] is 1 and word2[index] is also 1, then the result
	 * word[index] will be 1, because 2/3 words have a 1 bit at index.
	 * 
	 * @param word1
	 * @param word2
	 * @param word3
	 * @return
	 */
	public static int majority(int word1, int word2, int word3) {
		return (word1 & word2) ^ (word1 & word3) ^ (word2 & word3);
	}

	/**
	 * Upper case sigma zero operation.
	 * 
	 * @param word
	 * @return
	 */
	public static int Σ0(int word) {
		return rotr(word, 2) ^ rotr(word, 13) ^ rotr(word, 22);
	}

	/**
	 * Upper case sigma one operation.
	 * 
	 * @param word
	 * @return
	 */
	public static int Σ1(int word) {
		return rotr(word, 6) ^ rotr(word, 11) ^ rotr(word, 25);
	}

	/**
	 * Compresses a message into the initial hash register block by block. The hash
	 * register after every block and word have been compressed is returned.
	 * 
	 * @param message
	 * @return
	 */
	public static int[] compress(int[][] message) {
		// initialize the register
		int[] reg = new int[8];
		for (int i = 0; i < 8; i++) {
			reg[i] = initialRegister[i];
		}
		// compress each block
		for (int i = 0; i < message.length; i++) {
			// store initial register
			int[] initialRegister = reg.clone();
			// compress each word
			for (int j = 0; j < 64; j++) {
				updateRegister(reg, t1(reg, message[i][j], constants[j]), t2(reg));
			}
			// add the resulting register with the initial register
			for (int j = 0; j < 8; j++) {
				reg[j] = reg[j] + initialRegister[j];
			}
		}
		return reg;
	}

	/**
	 * Calculate the first temporary compression word.
	 * 
	 * @param register
	 * @param schedule
	 * @param constant
	 * @return
	 */
	public static int t1(int[] register, int schedule, int constant) {
		return Σ1(register[4]) + choice(register[4], register[5], register[6]) + register[7] + schedule + constant;
	}

	/**
	 * Calculate the second temporary compression word.
	 * 
	 * @param register
	 * @return
	 */
	public static int t2(int[] register) {
		return Σ0(register[0]) + majority(register[0], register[1], register[2]);
	}

	/**
	 * Update the register to compress the current word, using both temporary words. Each
	 * index in the register is shifted down leaving the first empty. The first index
	 * is set to the sum of both temporary words, while the 5th index has t1 added to its
	 * current value.
	 * 
	 * @param register
	 * @param t1
	 * @param t2
	 */
	public static void updateRegister(int[] register, int t1, int t2) {
		for (int i = 7; i > 0; i--) {
			register[i] = register[i - 1];
		}
		register[0] = t1 + t2;
		register[4] = register[4] + t1;
	}

	public static void main(String[] args) {
		
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < 8; i++) {
//			sb.append(Integer.toHexString(aRes[i]));
////			System.out.print(Integer.toHexString(aRes[i]));
//		}
//		System.out.println(sb);
	}
}
