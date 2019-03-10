import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A class that builds a mapping of search queries to search results
 * 
 * @author Ryan Dielhenn
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
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> words = TextFileStemmer.stemLineStream(line, stemmer)
						.collect(Collectors.toCollection(TreeSet::new));
				addQuery(words, exact);
			}

		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Searches the inverted index given a specified query and search type. Adds the
	 * query with its search results to the queryMap.
	 * 
	 * @param line
	 * @param exact
	 */
	public void addQuery(TreeSet<String> line, boolean exact) {
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