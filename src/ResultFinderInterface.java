import java.io.IOException;
import java.nio.file.Path;

/**
 * An interface for {@link ResultFinder} and {@link ThreadSafeResultFinder}
 * 
 * @author Ryan Dielhenn
 */
public interface ResultFinderInterface {

	/**
	 * Parses the queryFile and performs either exact or partial search on an
	 * inverted index, storing results in queryMap
	 * 
	 * @param queryFile - File of queries
	 * @param exact     - Exact or partial search
	 * @throws IOException
	 */
	public void parseQueries(Path queryFile, boolean exact) throws IOException;

	/**
	 * Writes mapping of queries to search results to .json format
	 * 
	 * @param outputFile - File to output to
	 * @throws IOException
	 */
	public void writeResults(Path outputFile) throws IOException;
}