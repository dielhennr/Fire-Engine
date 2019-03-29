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

	/** Number of threads to use */
	private int threads;

	/**
	 * @param index
	 * @param threads
	 */
	public ThreadSafeIndexBuilder(InvertedIndex index, int threads) {
		super(index);
		this.index = (ThreadSafeIndex) super.index;
		this.threads = threads;

	}

	public void build(Path start) throws IOException {
		TaskMaster master = new TaskMaster(TextFileFinder.list(start), index, this.threads);
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
	 * @author ryandielhenn
	 */
	private static class TaskMaster {

		/** Our list of paths to split up among threads */
		private final List<Path> paths;

		/** Our WorkQueue of threads */
		private final WorkQueue tasks;

		/** A reference to our thread safe index */
		private final ThreadSafeIndex index;

		/** The amount of unfinished work */
		private int pending;

		/**
		 * A Constructor for our task master
		 * 
		 * @param paths
		 * @param index
		 * @param threads
		 */
		private TaskMaster(List<Path> paths, ThreadSafeIndex index, int threads) {
			this.paths = paths;
			this.tasks = new WorkQueue(threads);
			this.index = index;
			this.pending = 0;
		}

		/**
		 * Fills our WorkQueue with runnable tasks
		 */
		private void start() {
			for (Path path : this.paths) {
				tasks.execute(new Task(path));
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
					InvertedIndexBuilder.buildFile(path, index);
				} catch (IOException e) {
					log.debug("Could not add " + path + " to the index");
				}
				decrementPending();
			}

		}

		/**
		 * Waits until TaskMaster has no more work
		 * 
		 * @throws InterruptedException
		 */
		private synchronized void join() throws InterruptedException {
			while (this.pending > 0) {
				this.wait();
				log.debug("Woke up with pending at {}.", pending);
			}
			log.debug("Work finished.");
		}

		/**
		 * Increments TaskMaster's pending work
		 */
		private synchronized void incrementPending() {
			this.pending++;
		}

		/**
		 * Decrements TaskMaster's pending work
		 */
		private synchronized void decrementPending() {
			assert this.pending > 0;
			this.pending--;
			/** If we have no more work, notify TaskMaster to wake up from join */
			if (pending == 0) {
				this.notifyAll();
			}
		}
	}

}