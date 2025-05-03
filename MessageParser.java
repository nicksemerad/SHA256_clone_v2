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

}
