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
	 * Parses an input string into a message of bits.
	 * 
	 * @param input
	 * @return
	 */
	public static BitSet parse(String input) {
		BitSet bits = stringToBits(input);
		int messageSize = messageSize(bits.length() + 64);
		String size = Integer.toBinaryString(bits.length() - 1);
		for (int i = 0; i < size.length(); i++) {
			if (size.charAt(size.length() - (1 + i)) == '1') {
				bits.set(messageSize - (1 + i));
			}
		}
		return bits;
	}

	/**
	 * Converts the input string into binary and uses it to construct a BitSet. The
	 * end of the input string binary is marked by a single 1 called the separator.
	 * 
	 * @param input
	 * @return
	 */
	private static BitSet stringToBits(String input) {
		int bitsLength = input.length() * 8;
		BitSet bits = new BitSet(bitsLength);
		for (int i = 0; i < input.length(); i++) {
			String charStr = Integer.toBinaryString(input.charAt(i));
			for (int j = 0; j < 8; j++) {
				int zeroes = 8 - charStr.length();
				if ((j >= zeroes) && (charStr.charAt(j - zeroes) == '1')) {
					bits.set((i * 8) + j);
				}
			}
		}
		// add the separator
		bits.set(bitsLength);
		return bits;
	}

	/**
	 * Returns the number of bits the final message will be.
	 * 
	 * @param length
	 * @return
	 */
	public static int messageSize(int length) {
		int additionalBits = length - (length % 512);
		return 512 + additionalBits;
	}

	/**
	 * 
	 * @return
	 */
	public static BitSet[] generateBlock() {
		BitSet[] block = new BitSet[64];
		for (int i = 0; i < 64; i++) {
			block[i] = new BitSet();
			block[i].set(31, false);
		}
		return block;
	}

	/**
	 * 
	 * 
	 * @param input
	 */
	public static void test(String input) {
		BitSet[] block = generateBlock();
		convertInput(block, input);
		seperate(block, input.length());
		convertSize(block, input.length() << 3);
		
		
//		block[input.length() / 4].set(input.length() << 3 % 32); // separator
		HashFunction.print(block[0]);	
		HashFunction.print(block[1]);	
		HashFunction.print(block[15]);
	}
	
	/**
	 * 
	 * 
	 * @param block
	 * @param input
	 */
	public static void convertInput(BitSet[] block, String input) {
		for (int i = 0; i < input.length(); i++) {
			String chr = Integer.toBinaryString(input.charAt(i));
			int start = (i * 8) % 32;
			start += 8 - chr.length(); // offset for leading 0s
			for (int j = 0; j < chr.length(); j++) {
				if (chr.charAt(j) == '1') {
					block[i / 4].set(start + j);
				}
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param block
	 * @param length
	 */
	public static void seperate(BitSet[] block, int length) {
		int bits = length << 3; // 8 bits per bite
		block[length >> 2].set(bits % 32);
	}
	
	/**
	 * 
	 * 
	 * @param block
	 * @param bitCount
	 */
	public static void convertSize(BitSet[] block, int bitCount) {
		String sizeBinary = Integer.toBinaryString(bitCount);
		int start = 64 - sizeBinary.length();
		for (int i = 0; i < sizeBinary.length(); i++) {
			start += i;
			if (sizeBinary.charAt(i) == '1') {
				int blockIdx = 14 + (start / 32); // BitSet in the block
				int idx = start % 32; // index in the BitSet
				block[blockIdx].set(idx);
			}
		}
	}
	

	public static void main(String[] args) {
		String input = "abcdef";
		test(input);
//		System.out.println();
//		BitSet res = parse(input);
//		HashFunction.print(res);
//		HashFunction.print(res.get(480, 512));

	}

}
