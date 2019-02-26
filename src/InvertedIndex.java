import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
	 * Stores a mapping of file locations to the number of unique positions 
	 * added to index.
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
	 * @param words array of words to add
	 * @param location the location that the words were found
	 * @param start starting position
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
	 * @param outputFile path to write output to
	 *
	 * @throws IOException 
	 */
	public void writeIndex(Path outputFile) throws IOException{
		PrettyJSONWriter.asDoubleNestedObject(this.index, outputFile);
	}

	/**
	 * Writes the locations with JSONWriter
	 * @param outputFile path to write output to
	 *
	 * @throws IOException 
	 */
	public void writeLocations(Path outputFile) throws IOException{ 
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
	 * Returns true if index is empty
	 * @return true if empty
	 */
	public boolean empty() {
		return this.index.size() == 0;
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
	 * Returns the number of positions stored in the index given a word and locations.
	 *
	 * @param word word to look for
	 * @param location location to look for
	 * @return number of times the word appears in a given location 
	 */
	public int numPositions(String word, String location) {
		return (index.containsKey(word) && index.get(word).containsKey(location)) ? index.get(word).get(location).size() : 0;
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
	 * @param word     word to look for
	 * @param file     position to look for word
	 * @return true    if the word is stored in the index at the specified position
	 */
	public boolean contains(String word, String file) {
		if (index.containsKey(word) && index.get(word).containsKey(file)) {
			return true;
		}
		return false;
	}

	/**
	 * Searches for queries in the inverted index and builds a list of SearchResults
	 * 
	 * @param line queries to search for
	 * @return results list of SearchResults
	 */
	public ArrayList<SearchResult> exactSearch(TreeSet<String> line) {
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		HashMap<String, SearchResult> resultMap = new HashMap<String, SearchResult>();
		
		for (String word : line) {
			if (index.containsKey(word)) {
				for (String file : index.get(word).keySet()) {
					if (!resultMap.containsKey(file)) {
						SearchResult result = new SearchResult(file, this.numPositions(word, file), this.locations.get(file));
						results.add(result);
						resultMap.put(file, result);
					}
					else {
						resultMap.get(file).updateCount(this.numPositions(word,file));
					}
				}
			}
		}
		
		Collections.sort(results);
		return results;
	}

	/**
	 * @param line
	 * @return results list of search results
	 */
	public ArrayList<SearchResult> partialSearch(TreeSet<String> line) {
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();
		HashMap<String, SearchResult> resultMap = new HashMap<String, SearchResult>();
		
		for (String query : line) {
			for (Entry<String, TreeMap<String, TreeSet<Integer>>> entry : index.entrySet()) {
				if (entry.getKey().startsWith(query)) {
					for (String file : entry.getValue().keySet()) {
						if (!resultMap.containsKey(file)) {
							SearchResult result = new SearchResult(file, this.numPositions(query, file), this.locations.get(file));
							results.add(result);
							resultMap.put(file, result);
						}
						else {
							resultMap.get(file).updateCount(this.numPositions(query,file));
						}
					}
					
				}
			}
			
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}
}
