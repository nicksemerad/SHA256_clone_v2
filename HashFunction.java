package nicks_hash_function;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HashFunction {

	/*
	 * Heavily a work in progress!!
	 * 
	 * note: Currently working on optimizing code organization and running time
	 * efficiency
	 * 
	 * TODO:
	 * 
	 * 1. change the parser and all methods to work with the BitSets being reversed,
	 * as it would prevent needing to re-reverse them when converting them back to
	 * longs for the add() methods
	 * 
	 * 
	 */

	public static void main(String[] args) {
		String input = "abc";
		HashRegister register = new HashRegister(input);
//		System.out.println(register.hash());

	}

	public static void print(LinkedList<BitSet> list) {
		Iterator<BitSet> itr = list.iterator();
		while (itr.hasNext()) {
			print(itr.next(), 32);
		}
	}

	public static void print(BitSet[] arr) {
		for (BitSet bits : arr) {
			if (bits != null) {
				print(bits, 32);
			}
		}
	}

	public static void print(BitSet bits, int size) {
		for (int i = 0; i < size; i++) {
			boolean val = bits.get(i);
			System.out.print(val ? 1 : 0);
		}
		System.out.print("\n");
	}

	public static void print(BitSet bits) {
		int words = Math.ceilDiv(bits.length(), 32);
		for (int i = 0; i < words; i++) {
			int idx = i * 32;
			print(bits.get(idx, idx + 32), 32);
		}
		System.out.print("\n");
	}
	
	public static void print(Message message) {
		for(int i = 0; i < message.size(); i++) {
			System.out.println("BLOCK " + i + ": ");
			print(message.getBlock(0));
		}
	}
	
}
