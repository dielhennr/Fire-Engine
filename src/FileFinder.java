import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * A class that recursively traverses a given directory and returns all text
 * files.
 * 
 * @author dielhennr
 */
public class FileFinder {

	/**
	 * Checks if the path is a path to a text file.
	 * 
	 * @param file
	 * @return true is file is a text file
	 */
	public static boolean isTextFile(Path file) {

		String fileName = file.getFileName().toString().toLowerCase();

		return fileName.endsWith(".txt") || fileName.endsWith(".text");

	}

	/**
	 * Takes a path to a directory as input and returns a list of paths to text
	 * files within this directory.
	 * 
	 * @param directory The directory being searched
	 * 
	 */
	public static ArrayList<Path> traverse(Path directory) {

		ArrayList<Path> pathList = new ArrayList<Path>();
		try {
			traverse(directory, pathList);
		} catch (IOException e) {
			System.err.println("problem searching directory");
		}
		return pathList;
	}

	/**
	 * Recursive helper method for traverse
	 * 
	 * @param directory The directory being searched
	 * @param pathList  The list of paths to text files
	 */
	public static void traverse(Path directory, ArrayList<Path> pathList) throws IOException {

		if (Files.isDirectory(directory)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
				for (Path file : listing) {

					traverse(file, pathList);
				}

			}
		} else if (isTextFile(directory)) {
			pathList.add(directory);
		}
	}
}