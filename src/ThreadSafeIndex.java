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

	/**
	 * @see InvertedIndex#add(String, String, int)
	 */
	@Override
	public boolean add(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			return super.add(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * @see InvertedIndex#addAll(List, String, int)
	 */
	@Override
	public boolean addAll(List<String> words, String location, int start) {
		lock.writeLock().lock();
		try {
			return super.addAll(words, location, start);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * @see InvertedIndex#writeIndex(Path)
	 */
	@Override
	public synchronized void writeIndex(Path outputFile) throws IOException {
		super.writeIndex(outputFile);
	}
	
	/**
	 * @see InvertedIndex#writeLocations(Path)
	 */
	@Override
	public synchronized void writeLocations(Path outputFile) throws IOException {
		super.writeLocations(outputFile);
	}

	/**
	 * @see InvertedIndex#numWords()
	 */
	@Override
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see InvertedIndex#empty()
	 */
	@Override
	public boolean empty() {
		lock.readLock().lock();
		try {
			return super.empty();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * @see InvertedIndex#numFiles(String)
	 */
	@Override
	public int numFiles(String word) {
		lock.readLock().lock();
		try {
			return super.numFiles(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * @see InvertedIndex#numPositions(String, String)
	 */
	@Override
	public int numPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.numPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * @see InvertedIndex#contains(String)
	 */
	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see InvertedIndex#contains(String, String)
	 */
	@Override
	public synchronized boolean contains(String word, String file) {
		lock.readLock().lock();
		try {
			return super.contains(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @see InvertedIndex#search(Collection, boolean)
	 */
	@Override
	public ArrayList<SearchResult> search(Collection<String> queries, boolean exact) {
		lock.readLock().lock();
		try {
			return super.search(queries, exact);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * @param local
	 */
	@Override
	public void addLocal(InvertedIndex local) {
		lock.writeLock().lock();
		try {
			super.addLocal(local);
		} 
		finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * @see InvertedIndex#toString()
	 */
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