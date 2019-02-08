import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Driver class
 * 
 * @author dielhennr
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
		
		
		boolean run = true;
		if (map.isEmpty() || !map.hasValue("-path") || !map.hasFlag("-index")) {
			run = false;
		}
		
		if (map.hasFlag("-index") && !map.hasFlag("-path")) {
			Path file = null;
			if (map.hasValue("-index")) {
				file = map.getPath("-index");
			}else {
				file = Paths.get("index.json");
			}
			run = false;
		}

		if (run) {
			Path inFile = map.getPath("-path");

			if (inFile.toFile().exists()) {

				ArrayList<Path> files = FileFinder.traverse(inFile);

				InvertedIndexBuilder builder = new InvertedIndexBuilder();
				builder.build(files, map);
			}

		}
		
		
	}

}
