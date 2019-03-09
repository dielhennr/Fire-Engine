import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Builds a data structure that stores words and their positions in files.
 *
 * @author Ryan Dielhenn
 */
public class InvertedIndexBuilder {

	/**
	 * Stores a reference to an InvertedIndex
	 */
	private final InvertedIndex index;

	/**
	 * Constructor
	 * 
	 * @param index
	 */
	public InvertedIndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Builds an InvertedIndex object from a list of files
	 *
	 * @param files
	 * @throws IOException
	 */
	public void build(List<Path> files) throws IOException {
		for (Path file : files) {
			InvertedIndexBuilder.buildFile(file, this.index);
		}
	}
	
	/* TODO
	public void build(Path start) throws IOException {
		build(TextFileFinder.list(start));
	}
	*/

	/**
	 * Adds stemmed words of one file to the Inverted Index
	 * 
	 * @param file
	 * @param index
	 * @throws IOException
	 */
	public static void buildFile(Path file, InvertedIndex index) throws IOException {
		int counter = 0;
		try (BufferedReader w = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

			String line;
			while ((line = w.readLine()) != null) {
				/* TODO
				 * 1) Every single line creates a stemmer object.
				 * Create 1 stemmer per file and reuse
				 * 
				 * 2) TextParser loops through the line.
				 * TextFileStemmer loops through the line.
				 * addAll loops through the line again.
				 * 
				 * reduce by 1 loop so you dont have the temporary list storage
				 */
				List<String> words = TextFileStemmer.stemLine(line);
				index.addAll(words, file.toString(), counter);
				counter += words.size();
			}

		} catch (IOException e) {
			throw e;
		}
	}
}