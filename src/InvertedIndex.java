import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.file.Path;
import java.util.List;

/**
 * Data structure to store strings and their positions.
 * 
 * @author dielhennr
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private TreeMap<String, TreeMap<String, TreeSet<Integer>>> index; // TODO Add the final

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
	}

	// TODO Update Javadoc
	// TODO Refactor file to location
	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param position position word was found
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String file, int position) {
		/*
		 * TODO: Make sure you initialize any inner data structures.
		 */
		word = word.trim(); // TODO Remove
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
		}
		
		if (!index.get(word).containsKey(file)) {
			index.get(word).put(file, new TreeSet<Integer>());

		}
		
		// TODO return index.get(word).get(file).add(position);
		if (!index.get(word).get(file).contains(position)) {
			index.get(word).get(file).add(position);
			return true;
		}
		
		return false;
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * the provided starting position
	 *
	 * @param words array of words to add
	 * @param start starting position
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 */
	public boolean addAll(List<String> words, Path file, int start) {
		/*
		 * TODO: Add each word using the start position. (You can call your other
		 * methods here.)
		 */
		boolean changed = false;
		for (String word : words) {
			if (this.add(word, file.toFile().toString(), ++start)) {
				changed = true;
			}
		}

		return changed;
	}

	// TODO Never return a reference to a private mutable object
	/**
	 * Returns the whole TreeMap
	 * 
	 * @return index
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> retrieve() {
		return index;
	}

	/* TODO
	public void toJSON(Path path) {
//		call TreeJSONWriter.asDoubleNestedObject(index, ...)
	}*/

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		return index.size();
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		// TODO Simplify
		if (index.containsKey(word)) {
			return true;
		}
		return false;
	}

	/**
	 * Tests whether the index contains the specified word at the specified
	 * position.
	 *
	 * @param word     word to look for
	 * @param position position to look for word
	 * @return true if the word is stored in the index at the specified position
	 */
	public boolean contains(String word, String file) {

		// avoid null pointer exception
		if (index.containsKey(word) && index.get(word).containsKey(file)) {
			return true;
		}
		return false;
	}
	
	// TODO Add additional methods for all levels of nesting

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

}
