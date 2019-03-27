import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A thread safe InvertedIndex
 * 
 * @author Ryan Dielhenn
 */
public class ThreadSafeIndex extends InvertedIndex {

	@Override
	public synchronized boolean add(String word, String location, int position) {
		return super.add(word, location, position);
	}

	@Override
	public synchronized boolean addAll(List<String> words, String location, int start) {
		return super.addAll(words, location, start);
	}

	@Override
	public synchronized void writeIndex(Path outputFile) throws IOException {
		super.writeIndex(outputFile);
	}

	@Override
	public synchronized void writeLocations(Path outputFile) throws IOException {
		super.writeLocations(outputFile);
	}

	@Override
	public synchronized int numWords() {
		return super.numWords();
	}

	@Override
	public synchronized boolean empty() {
		return super.empty();
	}

	@Override
	public synchronized int numFiles(String word) {
		return super.numFiles(word);
	}

	@Override
	public synchronized int numPositions(String word, String location) {
		return super.numPositions(word, location);
	}

	@Override
	public synchronized boolean contains(String word) {
		return super.contains(word);
	}

	@Override
	public synchronized boolean contains(String word, String file) {
		return super.contains(word, file);
	}

	@Override
	public synchronized ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {
		return super.search(queries, exact);
	}
	
	@Override
	public synchronized ArrayList<SearchResult> exactSearch(Collection<String> line) {
		return super.exactSearch(line);
	}

	@Override
	public synchronized ArrayList<SearchResult> partialSearch(Collection<String> queries) {
		return super.partialSearch(queries);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}
}