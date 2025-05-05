package nicks_hash_function;

import java.util.BitSet;

/**
 * This class represents the input message. The message BitSet is cut into 512
 * bit blocks. Each block is further cut into 16 BitSets at 32 bits each, called
 * words. Before these message blocks can be compressed into the hash register,
 * they need to be extended to 64 words. This is done by the propagate method
 * which collects some of the existing words, operates on them, adds them, and
 * places the resulting word back in the block. This is repeated until the block
 * is full.
 */
public class Message extends BitOperations {
	private Block[] blocks;
	private int size;

	/**
	 * Makes a new empty Message with a given size.
	 * 
	 * @param size
	 */
	public Message(int size) {
		blocks = new Block[size];
		this.size = size;
		for (int i = 0; i < size; i++) {
			blocks[i] = new Block();
		}
	}

	/**
	 * Propagates each of the message blocks to prepare them for compression.
	 */
	public void propogateBlocks() {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].propogate();
		}
	}

	/**
	 * Returns the block in this message at a specified index.
	 * 
	 * @param index
	 * @return
	 */
	public BitSet[] getBlock(int index) {
		return blocks[index].words;
	}

	/**
	 * Returns the word that a char at the provided index would fall in the message.
	 * 
	 * @param charIndex
	 * @return
	 */
	public BitSet getWord(int charIndex) {
		int block = charIndex / 64; // 64 chars per block
		int word = (charIndex / 4) % 16; // 4 chars per word, 16 words/ block
		return blocks[block].words[word];
	}

	/**
	 * Sets a bit in the word that a char at the provided index would fall in the
	 * message. Additionally takes in an offset value, which lets you set the value
	 * of a bit that would fall within a specific char index binary.
	 * 
	 * @param charIndex
	 * @param offset
	 */
	public void setWordBit(int charIndex, int offset) {
		int bit = (charIndex % 4) * 8;
		getWord(charIndex).set(bit + offset);
	}

	/**
	 * Sets a bit in the reserved portion of the message that falls at a specific
	 * index . The reserved portion is the last 2 words (64 bits) of the last block
	 * that hold the binary representation of the number of bits the input message
	 * takes up.
	 * 
	 * @param reservedIndex
	 */
	public void setReservedBit(int reservedIndex) {
		Block block = blocks[size - 1];
		BitSet word = reservedIndex < 31 ? block.words[14] : block.words[15];
		word.set(reservedIndex % 32);
	}

	/**
	 * Returns the number of blocks in this message.
	 * 
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * This class represents a single 512 bit block as an array of BitSets to make
	 * operations easier.
	 */
	private class Block {
		public BitSet[] words;

		/**
		 * Constructs a new block with the provided BitSet array.
		 * 
		 * @param words
		 */
		public Block() {
			BitSet[] block = new BitSet[64];
			for (int i = 0; i < 16; i++) {
				block[i] = new BitSet();
			}
			words = block;
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
