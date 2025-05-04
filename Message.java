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
	 * Makes a new Message by parsing the blocks and propagating them.
	 * 
	 * @param message
	 */
	public Message(BitSet message) {
		parseBlocks(message);
		propogateBlocks();
	}

	/**
	 * Makes a new empty Message with a given size.
	 * 
	 * @param size
	 */
	public Message(int size) {
		blocks = new Block[size];
		size = 0;
	}

	/**
	 * Parses the message blocks into block objects and places them in the message's
	 * block array.
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
	 * Makes a new block with the provided BitSet[] and places it in the blocks
	 * array at the first open index.
	 * 
	 * @param words
	 */
	public void addBlock(BitSet[] words) {
		Block newBlock = new Block(words);
		blocks[size] = newBlock;
		size++;
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
	 * Propagates each of the message blocks to prepare them for compression.
	 */
	public void propogateBlocks() {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i].propogate();
		}
	}

	/**
	 * Returns the number of blocks in this message. Note: not the length of the
	 * blocks array.
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
		 * Constructs a new block with the provided BitSet array.
		 * 
		 * @param words
		 */
		public Block(BitSet[] words) {
			this.words = words;
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
