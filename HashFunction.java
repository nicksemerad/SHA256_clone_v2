package nicks_hash_function;

import java.util.BitSet;

public class HashFunction {

	/*
	 * Heavily a work in progress!!
	 * 
	 * 
	 * 1. message to binary
	 * 2. add a one to the end to signify message end
	 * 3. pad with zeros to a multiple 512 bits - 64 bits for the length (Integer)
	 * 4. cut into blocks of 512 (message block)
	 * 5. cut each block into a message schedule (16 words 32 bits each)
	 * 6. propagate words with functions to convert the schedule to 64 words
	 * 7. compression component of the hash function (state registers- the sqrt of the first 8 primes)
	 * 8. for each word in the message schedule, take it and its corresponding constant 
	 * 		using these two words and the state registers, create the two temp words
	 * 9. compress the temporary words into the register, and move to the next message word
	 * 10. add the current state register to the original state register
	 * 11. if there are additional message blocks, use the result as the initial state register and repeat
	 * 12. convert each of the 8 final words to hexadecimal and concatenate them to get the final hash
	 * 
	 */

	public HashFunction(int index) {

	}

	private static short[] primes() {
		return new short[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89,
				97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197,
				199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311 };
	}

	public Word primeConstant(int index) {
		// long equal to 2^32 used to scale the constant to ~32 bits
		long scale = 1L << 32;
		// get the root result, modulus one to make any digits left of the decimal 0
		double root = (Math.cbrt(primes()[index]) % 1) * scale;
		// convert the double to a long and create a BitSet with it
		return new Word((long) root);
	}

	public static void main(String[] args) {
		String input = "abc";
		BitSet bits = new BitSet(input.length() * 8);
		char[] chars = input.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			String charStr = Integer.toBinaryString(Integer.valueOf(chars[i]));
			for (int j = 0; j < 8; j++) {
				int zeroes = 8 - charStr.length(); // 3
				if ((j >= zeroes) && (charStr.charAt(j - zeroes) == '1')) {
					bits.set((i * 8) + j);
				}
			}
		}
		for (int i = 0; i < input.length() * 8; i++) {
			boolean val = bits.get(i);
			System.out.print(val ? 1 : 0);
		}
		System.out.print("\n");

	}

}
