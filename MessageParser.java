package nicks_hash_function;

import java.util.BitSet;

/**
 * Parses an input string into a message. A message is a set of bits (0s or 1s)
 * that stores information about the input string. Every message has a length
 * that is divisible by 512 so it can always be broken down into 512 bit blocks.
 * 
 * A message begins with the string in binary. A separator is added to the end,
 * which is just a single 1 bit. This is followed by 0s which pad the message to
 * make sure the final length is a multiple of 512 bits. The padding continues
 * until there are only 64 bits left to hit its final size. (i.e. a message with
 * 512 bits will be padded from the separator to bit 448.) These last 64 bits
 * are reserved for the number of bits in the input string binary.
 * 
 * [input in binary][1][0000000000][size]
 */
public class MessageParser {

	/**
	 * Returns the number of blocks the message will need.
	 * 
	 * @param input
	 * @return
	 */
	public static int blocksNeeded(String input) {
		int inputBits = input.length() * 8;
		inputBits++; // separator
		inputBits += 64; // reserved for the message size
		return (inputBits / 512) + 1;
	}

	/**
	 * Builds the Message by breaking it into blocks.
	 * 
	 * @param input
	 * @return
	 */
	public static Message buildMessage(String input) {
		Message message = new Message(blocksNeeded(input));
		convertInput(message, input);
		separate(message, input);
		convertSize(message, input);
		message.propogateBlocks();
		return message;
	}

	/**
	 * Converts the input string into binary and adds it to the message block(s).
	 * 
	 * @param message
	 * @param input
	 */
	public static void convertInput(Message message, String input) {
		for (int i = 0; i < input.length(); i++) {
			String chr = Integer.toBinaryString(input.charAt(i));
			int offset = 8 - chr.length();
			for (int j = 0; j < chr.length(); j++) {
				if (chr.charAt(j) == '1') {
					message.setWordBit(i, offset + j);
				}
			}
		}
	}

	/**
	 * Adds the separator to the message right after the input string binary.
	 * 
	 * @param message
	 * @param input
	 */
	public static void separate(Message message, String input) {
		message.setWordBit(input.length(), 0);
	}

	/**
	 * Converts the length of the input string binary into binary itself, and adds
	 * it to the end of the message block.
	 * 
	 * @param message
	 * @param input
	 */
	public static void convertSize(Message message, String input) {
		String binary = Integer.toBinaryString(input.length() * 8);
		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '1') {
				message.setReservedBit((i + 64) - binary.length());
			}
		}
	}
	
	
	public static void print(int[][] message, Message message2) {
		for (int i = 0; i < message.length; i++) {
			for (int j = 0; j < 64; j++) {
				StringBuilder mIJ = new StringBuilder();
				String m = Integer.toBinaryString(message[i][j]);
				for(int k = 0; k < 32 - m.length(); k++) {
					mIJ.append("0");
				}
				mIJ.append(m);
				System.out.println(mIJ.toString());
				HashFunction.print(message2.getBlock(i)[j]);
				System.out.println();
			}
		}

	}
	
	
	
	public static void print(int[][] message) {
		for (int i = 0; i < message.length; i++) {
			for (int j = 0; j < 64; j++) {
				System.out.println(Integer.toBinaryString(message[i][j]));
			}
		}

	}

	public static char intVal(String s, int index) {
		return index < s.length() ? s.charAt(index) : 0;
	}

	public static int[][] buildMessage2(String s) {
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
	
	public static void propogate(int[][] message) {
		for(int i = 0; i < message.length; i++) {
			for(int j = 16; j < 64; j++) {
				int a = message[i][j-16];
				int b = σ0(message[i][j-15]);
				int c = message[i][j-7];
				int d = σ1(message[i][j-2]);
				message[i][j] = a + b + c + d;

			}
		}
	}
	
	public static int rotr(int word, int rotations) {
		int p1 = word >>> rotations;
		int p2 = word << (32 - rotations);	
		return p1 + p2;
	}
	
	public static int σ0(int word) {
		return rotr(word, 7) ^ rotr(word, 18) ^ (word >>> 3);
	}
	
	public static int σ1(int word) {
		return rotr(word, 17) ^ rotr(word, 19) ^ (word >>> 10);
	}
	
	public static int Σ0(int word) {
		return rotr(word, 2) ^ rotr(word, 13) ^ rotr(word, 22);
	}
	
	public static int Σ1(int word) {
		return rotr(word, 6) ^ rotr(word, 11) ^ rotr(word, 25);
	}
	
	public static void main(String[] args) {		
		String input = "abcd";
		int[][] a = buildMessage2(input);
		Message m = buildMessage(input);
		System.out.println();
		print(a, m);
		
		
	}

//	public static void main(String[] args) {
//		Message m = buildMessage("abcd");
//		HashFunction.print(m);
//		System.out.println();
//		HashFunction.print(BitOperations.add(m.getBlock(0)[0], BitOperations.getConstant(1)));
//		BitOperations.add(m.getBlock(0)[0], null);
//	}
}
