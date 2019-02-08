import java.io.IOException;
import java.nio.file.Files;
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
		InvertedIndex index = new InvertedIndex();
		
		
		boolean run = true;
		if (map.isEmpty() || !map.hasValue("-path") || !map.hasFlag("-index")) {
			run = false;
		}
		
		if (map.hasFlag("-index") && !map.hasFlag("-path")) {
			if (!map.hasValue("-index")) {
				try {
					Files.createFile(Paths.get("index.json"));
				} catch (IOException e) {
					System.err.println("Trouble outputting empty file");
				}
			}
			run = false;
		}

		if (run) {
			Path inFile = map.getPath("-path");

			if (inFile.toFile().exists()) {

				ArrayList<Path> files = FileFinder.traverse(inFile);

				InvertedIndexBuilder builder = new InvertedIndexBuilder();
				builder.build(files, index);
				
			}

		}
		if (map.hasFlag("-index")) {
			
			if (!map.hasValue("-index")) {
				index.writeIndex(Paths.get("index.json"));
			}else {
				index.writeIndex(map.getPath("-index"));
			}
		}
		
		if (map.hasFlag("-locations")) {
			if (!map.hasValue("-locations")) {
				index.writeLoc(Paths.get("locations.json"));
			}else {
				index.writeLoc(map.getPath("-locations"));
			}
		}
		
		
		
	}

}
