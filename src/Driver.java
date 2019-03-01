import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Parses the command-line arguments to build and use an in-memory search engine
 * from files or the web.
 *
 * @author Ryan Dielhenn
 */
public class Driver {

	// TODO Check Javadoc warnings

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();

		if (map.hasFlag("-path") && map.hasValue("-path")) {
			Path inFile = map.getPath("-path");

			if (Files.exists(inFile)) {
				List<Path> files = null;

				try {
					files = TextFileFinder.list(inFile);
				} catch (IOException ioe) {
					System.err.println("Issue finding a file");
				}

				try {
					InvertedIndexBuilder builder = new InvertedIndexBuilder(index);
					builder.build(files);
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
			Path path = map.getPath("-locations", Paths.get("locations.json"));
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

	}

}
