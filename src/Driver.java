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
				} 
				catch (IOException ioe) {
					System.err.println("Issue finding files");
				}
				
				InvertedIndexBuilder builder = new InvertedIndexBuilder();
				builder.build(files, index);

			}
			else {
				System.err.println("The provided path is invalid, it does not exist");
			}
		}

		if (map.hasFlag("-index")) {
			index.writeIndex(map.getPath("-index", Paths.get("index.json")));
		}

		if (map.hasFlag("-locations")) {
			index.writeLoc(map.getPath("-locations", Paths.get("locations.json")));

		}

	}

}
