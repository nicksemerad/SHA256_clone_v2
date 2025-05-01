package nicks_hash_function;

import java.util.BitSet;

public class HashFunction {

	/*
	 * 
	 * public String toBinary(String message) { StringBuilder binary = new
	 * StringBuilder(); for(int i = 0; i < message.length(); i++) { String binChar =
	 * 0 + Integer.toBinaryString((int)message.charAt(i)); while(binChar.length() <
	 * 8) binChar = 0 + binChar; binary.append(binChar); } return pad(binary); }
	 * 
	 * 
	 * 
	 * 
	 * Double result = (Math.sqrt(primes[i]) % 1) * Math.pow(2, 32); String data =
	 * String.format("%.0f", Math.floor(result)); String bits =
	 * Long.toBinaryString(Long.parseLong(data));
	 */

	/*
	 * 1. Get input string
	 * 
	 * 
	 */

	public HashFunction(int index) {

	}

	private static short[] primes() {
		return new short[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89,
				97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197,
				199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311 };
	}

	public static BitSet primeConstant(int index) {
		// long equal to 2^32 used to scale the constant to ~32 bits
		long scale = 1L << 32;
		// get the cube root result ignoring digits left of the decimal
		double cbrt = (Math.cbrt(primes()[index]) % 1) * scale;
		// get the binary string of the scaled cube root
		String binaryString = Long.toBinaryString((long) cbrt);
		BitSet bits = new BitSet(32);
		int numBits = binaryString.length();
		// fill the bit set using the binary string
		for (int i = 0; i < numBits; i++) {
			if (binaryString.charAt((numBits - 1) - i) == '1') {
				bits.set(31 - i);
			}
		}
		return bits;
	}

	public static void main(String[] args) {
		String input = "test";
		print(primeConstant(0));

	}

	public static void print(BitSet bits) {
		for (int i = 0; i < 32; i++) {
			boolean val = bits.get(i);
			System.out.print(val ? 1 : 0);
		}
	}

	private class Word {
		public BitSet bits;

		public Word(String s) {
			bits = new BitSet(32);
//			for (char c : s.toCharArray()) {
//				String bString = Integer.toBinaryString(c);
//			}

		}

	}

}
