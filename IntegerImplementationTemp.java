package nicks_hash_function;

/**
 * Temporary working space while I figure out how this will work using an
 * integer implementation.
 */
public class IntegerImplementationTemp {

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
				StringBuilder mIJ = new StringBuilder();
				String m = Integer.toBinaryString(message[i][j]);
				for (int k = 0; k < 32 - m.length(); k++) {
					mIJ.append("0");
				}
				mIJ.append(m);
				System.out.println(mIJ.toString());
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
				int a = message[i][j - 16];
				int b = σ0(message[i][j - 15]);
				int c = message[i][j - 7];
				int d = σ1(message[i][j - 2]);
				message[i][j] = a + b + c + d;

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

	public static void main(String[] args) {
		String input = "abcd";
		int[][] a = buildMessage(input);
//		Message m = buildMessage(input);
		System.out.println();
//		print(a, m);

	}
}
