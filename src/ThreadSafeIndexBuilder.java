import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A thread safe InvertedIndexBuilder
 * 
 * @author Ryan Dielhenn
 *
 */
public class ThreadSafeIndexBuilder extends InvertedIndexBuilder {

	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

	/** Reference to our thread safe index */
	private final ThreadSafeIndex index;

	/** Worker queue to use */
	private final WorkQueue workers;

	/**
	 * Constructor
	 * 
	 * @param index   - reference to the index we are building
	 * @param workers - reference to worker queue
	 */
	public ThreadSafeIndexBuilder(ThreadSafeIndex index, WorkQueue workers) {
		super(index);
		this.index = (ThreadSafeIndex) index; // TODO Remove cast
		this.workers = workers;

	}

	/**
	 * Builds out ThreadSafeIndex given a starting point in a file system
	 * 
	 * @param start
	 */
	public void build(Path start) throws IOException {

		for (Path path : TextFileFinder.list(start)) {
			workers.execute(new Task(path));
		}
		
		try {
			workers.join();
		} catch (InterruptedException e) {
			log.catching(Level.DEBUG, e);
		}
	}

	/**
	 * A task class that represents a piece of work for a thread to carry out
	 */
	private class Task implements Runnable {

		/**
		 * The path of the file to add to our ThreadSafeIndex
		 */
		private final Path path;

		/**
		 * Constructor for the Task, initializes the path object and increments
		 * TaskMaster's pending work
		 * 
		 * @param path - This task's path
		 */
		public Task(Path path) {
			this.path = path;
			log.debug("Task for {} created.", path);
		}

		/**
		 * Carries out the work and then decrements TaskMaster's pending work. In this
		 * case our work is to build a file into this thread's local index and then add
		 * this local index into the global index
		 */
		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.buildFile(path, local);
				index.addAll(local);
			} catch (IOException e) {
				log.debug("Could not add " + path + " to the index");
			}
		}
	}
}