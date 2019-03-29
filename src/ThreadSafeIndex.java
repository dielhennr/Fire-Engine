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

	/**
	 * The lock used to protect concurrent access to the underlying data structure.
	 */
	private SimpleReadWriteLock lock;

	/**
	 * Default constructor
	 */
	public ThreadSafeIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}

	@Override
	public boolean add(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			return super.add(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public boolean addAll(List<String> words, String location, int start) {
		lock.writeLock().lock();
		try {
			return super.addAll(words, location, start);
		} finally {
			lock.writeLock().unlock();
		}
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
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean empty() {
		lock.readLock().lock();
		try {
			return super.empty();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numFiles(String word) {
		lock.readLock().lock();
		try {
			return super.numFiles(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int numPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.numPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public synchronized boolean contains(String word, String file) {
		lock.readLock().lock();
		try {
			return super.contains(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {
		lock.readLock().lock();
		try {
			return super.search(queries, exact);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

}