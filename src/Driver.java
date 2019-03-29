import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parses the command-line arguments to build and use an in-memory search engine
 * from files or the web.
 *
 * @author Ryan Dielhenn
 */
public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index = null;
		InvertedIndexBuilder builder = null; 
		ResultFinder resultFinder = null;
		
		if (map.hasFlag("-threads")) {
			index = new ThreadSafeIndex();
			int threads = 5;
			if (map.hasValue("-threads")) {
				String tVal = map.getString("-threads");
				try {
					threads = Integer.parseInt(tVal); 
				} catch (NumberFormatException nfe) {
					threads = 5;
					System.err.println("Defaulting number of threads to 5. " + tVal + " is invalid.");
				}
				if (threads < 1) {
					threads = 5;
				}
			}
			builder = new ThreadSafeIndexBuilder(index, threads);
			resultFinder = new ThreadSafeResultFinder(index);	
		} else {
			index = new InvertedIndex();
			builder = new InvertedIndexBuilder(index);
			resultFinder = new ResultFinder(index);
		}
		
		if (map.hasFlag("-path") && map.hasValue("-path")) {
			Path inFile = map.getPath("-path");
			if (Files.exists(inFile)) {
				try {
					builder.build(inFile);
				} catch (IOException ioe) {
					System.err.println("Issue reading a file");
				}
			} else {
				System.err.println("The provided path is invalid, it does not exist");
			}
		} else if (map.hasFlag("-path") && !map.hasValue("-path")) {
			System.err.println("No path provided after the -path flag");
		}

		if (map.hasFlag("-index")) {
			Path path = map.getPath("-index", Paths.get("index.json"));
			try {
				index.writeIndex(path);
			} catch (IOException ioe) {
				System.err.println("Issue writing output to the specified -index file: " + path);
			}
		}

		if (map.hasFlag("-locations")) {
			Path path = map.getPath("-locations", Paths.get("locations.json"));
			try {
				index.writeLocations(path);
			} catch (IOException ioe) {
				System.err.println("Issue writing output to the specified -locations file: " + path);
			}
		}

		if (map.hasFlag("-query") && map.hasValue("-query")) {
			try {
				resultFinder.parseQueries(map.getPath("-query"), map.hasFlag("-exact"));
			} catch (IOException ioe) {
				System.err.println("Issue reading query file");
			}
		}

		if (map.hasFlag("-results")) {
			try {
				resultFinder.writeResults(map.getPath("-results", Paths.get("results.json")));
			} catch (IOException ioe) {
				System.err.println("Issue writing search result file");
			}
		}
	}

}
