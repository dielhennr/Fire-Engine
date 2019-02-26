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
	
	private final InvertedIndex index;
	private final TreeMap<String, ArrayList<SearchResult>> queryMap;
	
	/**
	 * @param index
	 * @param queryFile
	 * @param exact
	 */
	public ResultFinder(InvertedIndex index) {
		this.index = index;
		this.queryMap = new TreeMap<String, ArrayList<SearchResult>>();
	}
	
	/**
	 * Builds a tree set
	 * @param queryFile
	 * @param exact 
	 * @throws IOException
	 */
	public void addQueries(Path queryFile, boolean exact) throws IOException {
		
		try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				TreeSet<String> words = TextFileStemmer.stemQueryLine(line);
				addLine(words, exact);
			}

		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * @param line
	 * @param exact 
	 */
	public void addLine(TreeSet<String> line, boolean exact) {
		if (!line.isEmpty()) {
			String query = String.join(" ", line);
			if (exact) {
				queryMap.put(query, index.exactSearch(line));
			}
			else {
				queryMap.put(query, index.partialSearch(line));
			}
		}
	}
	
	
	/**
	 * @param outputFile
	 * @throws IOException 
	 */
	public void writeResults(Path outputFile) throws IOException {
		PrettyJSONWriter.asTripleNestedResultObject(this.queryMap, outputFile);
	}
	
}