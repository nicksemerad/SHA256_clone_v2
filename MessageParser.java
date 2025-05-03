package nicks_hash_function;

import java.util.BitSet;

/**
 * Provides functionality to take an input string and parse it into a message. A
 * message begins with the string in binary followed by a single 1 bit to mark
 * the end called the separator. The size of the binary for the string input is
 * stored in the last 64 bits of the message. Between the separator and the size
 * of the original message there are a number of zeroes called the padding.
 * These padding zeroes are added until the entire message has a number of bits
 * that is a multiple of 512.
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
