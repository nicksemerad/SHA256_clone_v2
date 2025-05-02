package nicks_hash_function;

import java.util.BitSet;

public class HashFunction {

	/*
	 * Heavily a work in progress!!
	 * 
	 * note: we are on step 7
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
	 * word 16: w0 + σ0(w1) + w9 + σ1(w14)
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

	public static void main(String[] args) {
		String input = "abc";
		HashRegister register = new HashRegister(input);

	}


	private static void printArr(BitSet[] arr) {
		for (BitSet bits : arr) {
			if (bits != null) {
				printBits(bits, 32);
			}
		}
	}

	public static void printBits(BitSet bits, int size) {
		for (int i = 0; i < size; i++) {
			boolean val = bits.get(i);
			System.out.print(val ? 1 : 0);
		}
		System.out.print("\n");
	}

	public static void printWords(BitSet bits) {
		int words = Math.ceilDiv(bits.length(), 32);
		for (int i = 0; i < words; i++) {
			int idx = i * 32;
			printBits(bits.get(idx, idx + 32), 32);
		}
		System.out.print("\n");
	}
}
