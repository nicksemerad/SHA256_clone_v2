package nicks_hash_function;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Contains the hash register and its functionality.
 */
public class HashRegister extends BitOperations {
	private LinkedList<BitSet> register;

	/**
	 * Makes a new HashRegister
	 * 
	 * @param input
	 */
	public HashRegister(String input) {
		initRegister();
		compress(MessageParser.parse(input));
//		HashFunction.printList(register);
		System.out.println(hash());
	}

	/**
	 * Calculates and returns the hash register, which is what the entire message is
	 * compressed into. This method only gets the initial register before any
	 * compression has happened. The initial register has 8 words of 32 bits each.
	 * Each word is the square root of the first 8 prime numbers, once they have
	 * been operated on to get the 32 bits of information.
	 * 
	 * @return
	 */
	public void initRegister() {
		register = new LinkedList<BitSet>();
		for (int i = 0; i < 8; i++) {
			String s = getRootBinaryString(Math.sqrt(getPrime(i)));
			BitSet bs = stringToBits(s);
			register.addLast(bs);
		}
	}

	/**
	 * Compresses each of the message blocks into the register.
	 * 
	 * @param message
	 */
	public void compress(BitSet messageBits) {
		Message message = new Message(messageBits);
		for (int i = 0; i < message.size; i++) {
			BitSet[] initialRegister = register.toArray(new BitSet[0]);
			compressBlock(message.blocks[i]);
			combine(initialRegister);
		}
	}

	/**
	 * Compresses a given block into the register.
	 * 
	 * @param block
	 */
	public void compressBlock(Block block) {
		for (int i = 0; i < 64; i++) {
			BitSet[] registerArray = register.toArray(new BitSet[0]);
			BitSet t1 = t1(registerArray, getConstant(i), block.words[i]);
			BitSet t2 = t2(registerArray);
			updateRegister(add(t1, t2), add(t1, registerArray[3]));
		}
	}

	/**
	 * Combines the hash register before the message block compression with the hash
	 * register after.
	 * 
	 * @param initialRegister
	 */
	public void combine(BitSet[] initialRegister) {
		ListIterator<BitSet> iter = register.listIterator();
		while (iter.hasNext()) {
			BitSet initalWord = initialRegister[iter.nextIndex()];
			BitSet resultWord = iter.next();
			iter.set(add(initalWord, resultWord));
		}
	}
	
	/**
	 * Calculates the first temporary word used in compression.
	 * 
	 * @param register
	 * @param constant
	 * @param schedule
	 * @return
	 */
	public BitSet t1(BitSet[] register, BitSet constant, BitSet schedule) {
		BitSet[] components = new BitSet[] { 
				Σ1(register[4]), 
				choice(register[4], register[5], register[6]),
				register[7], 
				schedule, constant 
		};
		return add(components);
	}

	/**
	 * Calculates the second temporary word used in compression.
	 * 
	 * @param register
	 * @return
	 */
	public BitSet t2(BitSet[] register) {
		BitSet[] components = new BitSet[] { 
				Σ0(register[0]), 
				majority(register[0], register[1], register[2]) 
		};
		return add(components);
	}

	/**
	 * Updates the register with the temporary compression words. Both temporary
	 * words are added together and placed the result is placed at the front of the
	 * list. The fourth element in the list has the first temporary word added to
	 * it. The last list element is removed to keep the list at length 8.
	 * 
	 * @param newWord
	 * @param updatedWord
	 */
	public void updateRegister(BitSet newWord, BitSet updatedWord) {
		register.addFirst(newWord);
		register.set(4, updatedWord);
		register.removeLast();
	}

	/**
	 * Converts the register to hexadecimal, and returns the hash value for the
	 * input as a string.
	 */
	public String hash() {
		StringBuilder hash = new StringBuilder();
		ListIterator<BitSet> iter = register.listIterator();
		while (iter.hasNext()) {
			long wordValue = bitsToLong(iter.next());
			hash.append(Long.toHexString(wordValue));
		}
		return hash.toString();
	}

	/**
	 * This class represents the input message cut up into 512 bit blocks, 
	 * each of which are propagated to prepare them for compression.
	 */
	private class Message {
		public Block[] blocks;
		public int size;

		/**
		 * Makes a new Message by parsing the blocks and propagating them.
		 * 
		 * @param message
		 */
		public Message(BitSet message) {
			parseBlocks(message);
			propogateBlocks();
		}

		/**
		 * Parses the message blocks into block objects and places them 
		 * in the message's block array.
		 * 
		 * @param message
		 */
		public void parseBlocks(BitSet message) {
			size = (message.length() / 512) + 1;
			blocks = new Block[size];
			for (int i = 0; i < size; i++) {
				blocks[i] = new Block(message.get(i * 512, (i + 1) * 512));
			}
		}

		/**
		 * Propagates each of the message blocks to prepare them for 
		 * compression.
		 */
		public void propogateBlocks() {
			for (int i = 0; i < blocks.length; i++) {
				blocks[i].propogate();
			}
		}
	}

	/**
	 * This class represents a single 512 bit block, which is an array of BitSets.
	 */
	private class Block {
		public BitSet[] words;

		/**
		 * Constructs a new block with the provided BitSet.
		 * 
		 * @param blockBits
		 */
		public Block(BitSet blockBits) {
			words = new BitSet[64];
			for (int i = 0; i < 16; i++) {
				words[i] = blockBits.get((i * 32), ((i + 1) * 32));
			}
		}

		/**
		 * Propagates a message block from 16 words (512 bits) to 64 words (2048 bits)
		 */
		public void propogate() {
			for (int i = 16; i < 64; i++) {
				BitSet[] components = new BitSet[] { 
						words[i - 16], 
						σ0(words[i - 15]), 
						words[i - 7], 
						σ1(words[i - 2]) 
				};
				words[i] = add(components);
			}
		}
	}
}