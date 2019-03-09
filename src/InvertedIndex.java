import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 *
 * @author Ryan Dielhenn
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Stores a mapping of file locations to the number of unique positions added to
	 * index.
	 */
	private final TreeMap<String, Integer> locations;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.locations = new TreeMap<String, Integer>();
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param location the path to the file that the word was found in
	 * @param position position word was found
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String location, int position) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(location, new TreeSet<Integer>());
		if (index.get(word).get(location).add(position)) {
			locations.put(location, locations.getOrDefault(location, 0) + 1);
			return true;
		}
		return false;
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * the provided starting position
	 *
	 * @param words    array of words to add
	 * @param location the location that the words were found
	 * @param start    starting position
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 */
	public boolean addAll(List<String> words, String location, int start) {
		boolean changed = false;
		for (String word : words) {
			if (this.add(word, location, ++start)) {
				changed = true;
			}
		}

		return changed;
	}

	/**
	 * Writes the Inverted Index with JSONWriter
	 * 
	 * @param outputFile path to write output to
	 *
	 * @throws IOException
	 */
	public void writeIndex(Path outputFile) throws IOException {
		PrettyJSONWriter.asDoubleNestedObject(this.index, outputFile);
	}

	/**
	 * Writes the locations with JSONWriter
	 * 
	 * @param outputFile path to write output to
	 *
	 * @throws IOException
	 */
	public void writeLocations(Path outputFile) throws IOException {
		PrettyJSONWriter.asObject(this.locations, outputFile);
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int numWords() {
		return index.size();
	}

	/**
	 * Returns the number of locations stored in the index for a given word.
	 * 
	 * @param word word to look for
	 * @return number of locations the word was found in
	 */
	public int numFiles(String word) {
		return index.containsKey(word) ? index.get(word).size() : 0;
	}

	/**
	 * Returns the number of positions stored in the index given a word and
	 * locations.
	 *
	 * @param word     word to look for
	 * @param location location to look for
	 * @return number of times the word appears in a given location
	 */
	public int numPositions(String word, String location) {
		return (index.containsKey(word) && index.get(word).containsKey(location)) ? index.get(word).get(location).size()
				: 0;
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		return index.containsKey(word);
	}

	/**
	 * Tests whether the index contains the specified word at the specified
	 * position.
	 *
	 * @param word word to look for
	 * @param file position to look for word
	 * @return true if the word is stored in the index at the specified position
	 */
	public boolean contains(String word, String file) {
		return (index.containsKey(word) && index.get(word).containsKey(file));
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

}
