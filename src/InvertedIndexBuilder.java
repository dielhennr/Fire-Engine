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
	 * Builds an InvertedIndex object from a list of files
	 *
	 * @param files
	 * @param index
	 * @throws IOException
	 */
	public static void build(List<Path> files, InvertedIndex index) throws IOException {
		for (Path file : files) {

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
	
}