import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

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

	/**
	 * Builds an InvertedIndex object from a given starting path
	 * 
	 * @param start
	 * @throws IOException
	 */
	public void build(Path start) throws IOException {
		build(TextFileFinder.list(start));
	}

	/**
	 * Adds stemmed words of one file to the Inverted Index
	 * 
	 * @param file
	 * @param index
	 * @throws IOException
	 */
	public static void buildFile(Path file, InvertedIndex index) throws IOException {
		AtomicInteger count = new AtomicInteger();
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader w = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

			String line;
			while ((line = w.readLine()) != null) {
				// TODO Parse here, and then loop through the array, stem, and add
				TextFileStemmer.stemLineStream(line, stemmer).forEach(word -> {
					index.add(word, file.toString(), count.getAndIncrement() + 1);
				});
			}

		} catch (IOException e) {
			throw e;
		}
	}
}