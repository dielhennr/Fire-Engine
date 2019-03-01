import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * functions and streams.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2019
 */
public class TextFileFinder {

	// TODO Could, not required, make TEXT_EXT a bi-predicate
	// TODO Slightly ineffiecent to call file.toString().toLowerCase() twice

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive).
	 *
	 * @see Files#isRegularFile(Path, java.nio.file.LinkOption...)
	 */
	public static final Predicate<Path> TEXT_EXT = (file) -> Files.isRegularFile(file)
			&& (file.toString().toLowerCase().endsWith(".txt") || file.toString().toLowerCase().endsWith(".text"));

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 *
	 * @throws IOException
	 *
	 * @see #TEXT_EXT
	 * @see FileVisitOption#FOLLOW_LINKS
	 *
	 * @see Files#walk(Path, FileVisitOption...)
	 * @see Files#find(Path, int, java.util.function.BiPredicate,
	 *      FileVisitOption...)
	 *
	 * @see Integer#MAX_VALUE
	 */
	public static Stream<Path> find(Path start) throws IOException {
		return Files.find(start, Integer.MAX_VALUE, (path, attribute) -> TEXT_EXT.test(path),
				FileVisitOption.FOLLOW_LINKS);
	}

	/**
	 * Returns a list of text files.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException
	 *
	 * @see #find(Path)
	 */
	public static List<Path> list(Path start) throws IOException {
		ArrayList<Path> pathList = new ArrayList<Path>();
		TextFileFinder.find(start).forEach(e -> pathList.add(e));
		return pathList;

		// TODO return TextFileFinder.find(start).collect(Collectors.toList());
	}

	/**
	 * Demonstrates this class.
	 *
	 * @param args unused
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Path path = Path.of(".", "text", "simple").normalize();
		list(path).forEach(System.out::println);
	}
}
