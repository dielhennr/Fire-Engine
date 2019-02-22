import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Driver class
 *
 * @author dielhennr // TODO Add full name here
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

			// TODO Files.exists(inFile)
			if (inFile.toFile().exists()) {

				ArrayList<Path> files = FileFinder.traverse(inFile);

				InvertedIndexBuilder builder = new InvertedIndexBuilder();
				builder.build(files, index);

			}
			// TODO else let user know path was invalid
		}

		if (map.hasFlag("-index")) {
			index.writeIndex(map.getPath("-index", Paths.get("index.json")));
		}

		if (map.hasFlag("-locations")) {
			index.writeLoc(map.getPath("-locations", Paths.get("locations.json")));

		}

	}

}
