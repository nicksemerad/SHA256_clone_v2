package nicks_hash_function;

/**
 * This class is a clone of the famous SHA256 hashing algorithm. It can be used
 * by passing in your desired input string as the first command line argument.
 * 
 * @author Nick Semerad
 * @version May 7, 2025
 */
public class HashFunction {

	/*
	 * These integers are the initial values of the hash register, and are the
	 * values the input is compressed into. They are found by taking the decimal
	 * portion of the square root of the first 8 primes and multiplying them by
	 * 2^32.
	 */
	private static int[] initialRegister = new int[] { 0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f,
			0x9b05688c, 0x1f83d9ab, 0x5be0cd19 };

	/**
	 * These integers are the constants used to compress the words in each message
	 * block. They are found by taking the decimal portion of the cube root of the
	 * first 64 primes and multiplying them by 2^32. Each of the 64 integer words in
	 * a given message block is compressed using the constant at the corresponding
	 * index. i.e. the first word at index zero is compressed using the first
	 * integer in the constants array, also at index zero.
	 */
	private static int[] constants = new int[] { 0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1,
			0x923f82a4, 0xab1c5ed5, 0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7,
			0xc19bf174, 0xe49b69c1, 0xefbe4786, 0xfc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
			0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x6ca6351, 0x14292967, 0x27b70a85,
			0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b,
			0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070, 0x19a4c116, 0x1e376c08, 0x2748774c,
			0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
			0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2 };

	/**
	 * Returns the character at a specific index in the string provided that it is
	 * not out of the bounds of the string. If it is, 0 is returned instead.
	 * 
	 * @param s     - string to find the char in
	 * @param index - index of the char to find
	 * @return char that was found at the index, or 0 if index was out of bounds
	 */
	private static char intVal(String s, int index) {
		return index < s.length() ? s.charAt(index) : 0;
	}

	/**
	 * Returns the SHA256 hash value for the provided input String. The input is
	 * first used to build a message. Each message block is then propagated and
	 * compressed into the initial hash register. The final hash register words are
	 * converted into hexadecimal and concatenated to produce the input string hash.
	 * 
	 * @param input - string to find the SHA256 hash value of
	 * @return the hash for the input string
	 */
	public static String hash(String input) {
		// add a '1' to the end of the input string to mark the end of the input
		input += (char) 128;
		// total words with each word holding 4 chars, plus 2 for the input length
		int words = input.length() / 4 + 2;
		// total blocks with each block holding 16 words
		int blocks = words / 16 + 1;
		int[][] message = new int[blocks][];
		// fill each block with 16 words / 64 characters
		for (int i = 0; i < blocks; i++) {
			message[i] = new int[64];
			for (int j = 0; j < 16; j++) {
				/*
				 * Take 4 words at a time and convert them to their integer values. Each char is
				 * 8 bits, shift the first value over 24 times to make room for the remaining 3
				 * chars (24 bits). Continue this process so all 4 char values are stored in a
				 * single 32 bit integer.
				 */
				int idx = i * 64 + j * 4;
				message[i][j] = (intVal(input, idx) << 24) + (intVal(input, idx + 1) << 16)
						+ (intVal(input, idx + 2) << 8) + (intVal(input, idx + 3) << 0);
			}
		}
		// separate the input binary length into two integers
		long bits = (input.length() - 1) * 8;
		long bitsHigh = bits >>> 32;
		long bitsLow = bits % (2L << 32);
		// place the two length integers into the last two words of the last block
		message[blocks - 1][14] = (int) bitsHigh;
		message[blocks - 1][15] = (int) bitsLow;
		// propagate the blocks, with each going from 16 words to 64
		propagate(message);
		int[] register = compress(message);
		return registerToHex(register);
	}

	/**
	 * Propagates each message block from 16 words/ integers to 64. Each new word is
	 * found using a combination of the existing block words, and existing block
	 * words after custom operations
	 * 
	 * @param message - message with blocks to be propagated
	 */
	private static void propagate(int[][] message) {
		for (int i = 0; i < message.length; i++) {
			for (int j = 16; j < 64; j++) {
				message[i][j] = message[i][j - 16] + σ0(message[i][j - 15]) + message[i][j - 7] + σ1(message[i][j - 2]);
			}
		}
	}

	/**
	 * Performs a right rotation on the provided integer. This shifts each bit in
	 * the integer to the right with the bits wrapping around to the left side.
	 * 
	 * @param word      - integer word to shift right
	 * @param rotations - the number of right rotations to perform
	 * @return the integer after the right rotations have been performed
	 */
	private static int rotr(int word, int rotations) {
		int p1 = word >>> rotations;
		int p2 = word << (32 - rotations);
		return p1 + p2;
	}

	/**
	 * Lower case sigma zero operation. Takes the given word and performs three
	 * operations: rotr 7 positions, rotr 18 positions, and shifts right 3
	 * positions. Each of the resulting words are XOR'd together and the result
	 * returned.
	 * 
	 * @param word - the integer to perform the operation on
	 * @return resulting integer of the operation
	 */
	private static int σ0(int word) {
		return rotr(word, 7) ^ rotr(word, 18) ^ (word >>> 3);
	}

	/**
	 * Lower case sigma one operation. Takes the given word and performs three
	 * operations: rotr 17 positions, rotr 19 positions, and shifts right 10
	 * positions. Each of the resulting words are XOR'd together and the result
	 * returned.
	 * 
	 * @param word - the integer to perform the operation on
	 * @return resulting integer of the operation
	 */
	private static int σ1(int word) {
		return rotr(word, 17) ^ rotr(word, 19) ^ (word >>> 10);
	}

	/**
	 * Upper case sigma zero operation. Takes the given word and performs three
	 * operations: rotr 2 positions, rotr 13 positions, and rotr 22 positions. Each
	 * of the resulting words are XOR'd together and the result returned.
	 * 
	 * @param word - the integer to perform the operation on
	 * @return resulting integer of the operation
	 */
	private static int Σ0(int word) {
		return rotr(word, 2) ^ rotr(word, 13) ^ rotr(word, 22);
	}

	/**
	 * Upper case sigma one operation. Takes the given word and performs three
	 * operations: rotr 6 positions, rotr 11 positions, and rotr 25 positions. Each
	 * of the resulting words are XOR'd together and the result returned.
	 * 
	 * @param word - the integer to perform the operation on
	 * @return resulting integer of the operation
	 */
	private static int Σ1(int word) {
		return rotr(word, 6) ^ rotr(word, 11) ^ rotr(word, 25);
	}

	/**
	 * Performs a choice operation on three integer words. Using the bits in the
	 * first word, the result word is decided. If word1[index] is 1, the bit at
	 * word2[index] is taken. If it is 0, word3[index] is taken instead.
	 * 
	 * @param word1 - first word to operate on
	 * @param word2 - second word to operate on
	 * @param word3 - third word to operate on
	 * @return resulting integer of the operation
	 */
	private static int choice(int word1, int word2, int word3) {
		return (word1 & word2) ^ (~word1 & word3);
	}

	/**
	 * Performs a majority operation on three integer words. The bits in each word
	 * are taken, and the majority at each index is the result bit in the output
	 * word. If word1[index] is 1 and word2[index] is also 1, then the result
	 * word[index] will be 1, because 2/3 words have a 1 bit at index.
	 * 
	 * @param word1 - first word to operate on
	 * @param word2 - second word to operate on
	 * @param word3 - third word to operate on
	 * @return resulting integer of the operation
	 */
	private static int majority(int word1, int word2, int word3) {
		return (word1 & word2) ^ (word1 & word3) ^ (word2 & word3);
	}

	/**
	 * Compresses a message into the initial hash register block by block. The hash
	 * register after every block/ word has been compressed is returned.
	 * 
	 * @param message - the message with the blocks to compress into the register
	 * @return the hash register once all the message blocks have been compressed
	 */
	private static int[] compress(int[][] message) {
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
	 * @param register - the current hash register
	 * @param schedule - the message block word currently in the schedule
	 * @param constant - the constant word that corresponds with the schedule word
	 * @return the first temporary word to use for compressing the current schedule word
	 */
	private static int t1(int[] register, int schedule, int constant) {
		return Σ1(register[4]) + choice(register[4], register[5], register[6]) + register[7] + schedule + constant;
	}

	/**
	 * Calculate the second temporary compression word.
	 * 
	 * @param register - the current hash register
	 * @return the second temporary word to use for compressing the current schedule word
	 */
	private static int t2(int[] register) {
		return Σ0(register[0]) + majority(register[0], register[1], register[2]);
	}

	/**
	 * Update the register to compress the current word, using both temporary words.
	 * Each index in the register is shifted down leaving the first empty. The first
	 * index is set to the sum of both temporary words, while the 5th index has t1
	 * added to its current value.
	 * 
	 * @param register - the current hash register
	 * @param t1 - the first temporary word used for compression
	 * @param t2 - the second temporary word used for compression
	 */
	private static void updateRegister(int[] register, int t1, int t2) {
		for (int i = 7; i > 0; i--) {
			register[i] = register[i - 1];
		}
		register[0] = t1 + t2;
		register[4] = register[4] + t1;
	}

	/**
	 * Converts the hash register to hexadecimal values and concatenates them to
	 * produce the final input string hash.
	 * 
	 * @param register - the hash register with the words to convert into hexadecimal
	 * @return the register words in hexadecimal concatenated, producing the input hash
	 */
	private static String registerToHex(int[] register) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			sb.append(Integer.toHexString(register[i]));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String input;
		if (args.length > 1) {
			input = args[0];
		} else {
			input = "abc";
		}
		System.out.println(hash(input));
	}

}
