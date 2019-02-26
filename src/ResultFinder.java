import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Ryan Dielhenn
 *
 */
public class ResultFinder {

	/**
	 * Reference to index so we can perform searches.
	 */
	private final InvertedIndex index;

	/**
	 * Queries mapped to search results found from search
	 */
	private final TreeMap<String, ArrayList<SearchResult>> queryMap;

	/**
	 * Constructor
	 * 
	 * @param index
	 */
	public ResultFinder(InvertedIndex index) {
		this.index = index;
		this.queryMap = new TreeMap<String, ArrayList<SearchResult>>();
	}

	/**
	 * Parses a query file and builds a map of queries to list of search results
	 * 
	 * @param queryFile
	 * @param exact
	 * @throws IOException
	 */
	public void parseQueries(Path queryFile, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> words = TextFileStemmer.stemQueryLine(line);
				addEntry(words, exact);
			}

		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Searches the inverted index given a specified query and search type
	 * 
	 * @param line
	 * @param exact
	 */
	public void addEntry(TreeSet<String> line, boolean exact) {
		if (!line.isEmpty()) {
			String query = String.join(" ", line);
			if (exact) {
				queryMap.put(query, index.exactSearch(line));
			} else {
				queryMap.put(query, index.partialSearch(line));
			}
		}
	}

	/**
	 * Writes mapping of queries to search results to .json format
	 * 
	 * @param outputFile
	 * @throws IOException
	 */
	public void writeResults(Path outputFile) throws IOException {
		PrettyJSONWriter.asResultObject(this.queryMap, outputFile);
	}

}