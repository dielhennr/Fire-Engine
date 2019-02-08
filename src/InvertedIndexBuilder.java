import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a data structure that stores words and their positions in files.
 * 
 * @author dielhennr
 */
public class InvertedIndexBuilder {

	private InvertedIndex index;

	/**
	 * Builds an InvertedIndex object from a list of files
	 * 
	 * @param files
	 * @param map
	 */
	public void build(ArrayList<Path> files, ArgumentMap map) {

		index = new InvertedIndex();

		for (Path file : files) {
			File f = file.toFile();
			int counter = 0;
			try (BufferedReader w = new BufferedReader(new FileReader(f))) {

				String line;
				while ((line = w.readLine()) != null) {
					List<String> words = TextFileStemmer.stemLine(line);
					index.addAll(words, file, counter);
					counter += words.size();
				}

			} catch (IOException e) {
				//
				e.printStackTrace();
			}
		}
		
		if (map.hasFlag("-index")) {
			
			if (!map.hasValue("-index")) {
				write(Paths.get("index.json"));
			}else {
				write(map.getPath("-index"));
			}
		}
		

	}

	/**
	 * Writes an InvertedIndex to a JSON file
	 * 
	 * @param outputFile
	 */
	public void write(Path outputFile) {
		try {
			PrettyJSONWriter.asDoubleNestedObject(index.retrieve(), outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}