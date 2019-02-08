import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a data structure that stores words and their positions in files.
 * 
 * @author dielhennr
 */
public class InvertedIndexBuilder {


	/**
	 * Builds an InvertedIndex object from a list of files
	 * 
	 * @param files
	 * @param index
	 */
	public void build(ArrayList<Path> files, InvertedIndex index) {

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
		
		

	}

	

}