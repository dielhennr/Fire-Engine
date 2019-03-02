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
				List<String> words = TextFileStemmer.stemLine(line);
				index.addAll(words, file.toString(), counter);
				counter += words.size();
			}

		} catch (IOException e) {
			throw e;
		}
	}
}