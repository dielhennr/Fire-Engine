import java.io.IOException;
import java.nio.file.Path;

/**
 * @author ryandielhenn
 *
 */
public interface ResultFinderInterface {
	
	
	/**
	 * @param queryFile
	 * @param exact
	 * @throws IOException
	 */
	public void parseQueries(Path queryFile, boolean exact) throws IOException;
	
	/**
	 * Writes mapping of queries to search results to .json format
	 * 
	 * @param outputFile
	 * @throws IOException
	 */
	public void writeResults(Path outputFile) throws IOException;
}