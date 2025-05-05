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

}
