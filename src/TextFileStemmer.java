import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class contains methods for stemming text.
 *
 * @author Ryan Dielhenn
 *
 */
public class TextFileStemmer {

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English
	 * {@link opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM} for
	 * stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static List<String> stemLine(String line) {
		return stemLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		return TextFileStemmer.stemLineStream(line).map(word -> (String) stemmer.stem(word))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a set of cleaned and stemmed words parsed from the provided line.
	 * Uses the English
	 * {@link opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM} for
	 * stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return set of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static Set<String> stemQueryLine(String line) {
		return stemQueryLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a set of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return set of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static Set<String> stemQueryLine(String line, Stemmer stemmer) {
		// TODO Downcast! stemmer.stem(word).toString()
		return TextFileStemmer.stemLineStream(line).map(word -> (String) stemmer.stem(word))
				.collect(Collectors.toSet());
	}

	/**
	 * Returns a stream of a parsed line
	 * 
	 * @param line
	 * @return stream of words in line
	 */
	public static Stream<String> stemLineStream(String line) {
		return Arrays.stream(TextParser.parse(line));
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then writes that line to a new file.
	 *
	 * @param inputFile  the input file to parse
	 * @param outputFile the output file to write the cleaned and stemmed words
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static void stemFile(Path inputFile, Path outputFile) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {

			String line;
			while ((line = reader.readLine()) != null) {
				List<String> stemmedLine = stemLine(line);
				
				// TODO Here might not make sense to use a stream
				stemmedLine.stream().forEach(e -> {
					try {
						writer.write(e + " ");
					} catch (IOException e1) {
						// Ask Sophie why this needs try/catch even if the exception is thrown in method
						// declaration
						System.err.println("Could not write a word to the output file");
					}
				});
				writer.newLine();
			}
		}
	}

	/**
	 * Uses {@link #stemFile(Path, Path)} to stem a single hard-coded file. Useful
	 * for development.
	 *
	 * @param args unused
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Path inputPath = Paths.get("test", "words.tExT");
		Path outputPath = Paths.get("out", "words.tExT");

		Files.createDirectories(Paths.get("out"));

		System.out.println(inputPath);
		stemFile(inputPath, outputPath);
	}
}
