import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
	 * @param index
	 * @param workers
	 */
	public ThreadSafeIndexBuilder(InvertedIndex index, WorkQueue workers) {
		super(index);
		this.index = (ThreadSafeIndex) super.index;
		this.workers = workers;

	}

	public void build(Path start) throws IOException {
		TaskMaster master = new TaskMaster(TextFileFinder.list(start), index, this.workers);
		master.start();

		try {
			master.join();
		} catch (InterruptedException e) {
			log.catching(Level.DEBUG, e);
		}

	}

	/**
	 * Adds tasks to WorkQueue and keeps track of pending work
	 * 
	 * @see WorkTracker
	 * @author ryandielhenn
	 */
	private static class TaskMaster extends WorkTracker {

		/** Our list of paths to split up among threads */
		private final List<Path> paths;

		/** Our WorkQueue of threads */
		private final WorkQueue workers;

		/** A reference to our thread safe index */
		private final ThreadSafeIndex index;

		/**
		 * A Constructor for our task master
		 * 
		 * @param paths
		 * @param index
		 * @param workers
		 */
		private TaskMaster(List<Path> paths, ThreadSafeIndex index, WorkQueue workers) {
			super();
			this.paths = paths;
			this.workers = workers; 
			this.index = index;
		}

		/**
		 * Fills our WorkQueue with runnable tasks
		 */
		private void start() {
			for (Path path : this.paths) {
				workers.execute(new Task(path));
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
			 * @param path
			 */
			public Task(Path path) {
				this.path = path;
				incrementPending();
				log.debug("Task for {} created.", path);
			}

			/**
			 * Carries out the work and then decrements TaskMaster's pending work
			 */
			@Override
			public void run() {
				try {
					InvertedIndex local = new InvertedIndex();
					InvertedIndexBuilder.buildFile(path, local);
					synchronized(index) {
						index.addLocal(local);
					}
				} catch (IOException e) {
					log.debug("Could not add " + path + " to the index");
				}
				decrementPending();
			}

		}

	}

}