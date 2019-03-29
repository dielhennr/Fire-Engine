import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A thread safe InvertedIndexBuilder
 * @author Ryan Dielhenn
 *
 */
public class ThreadSafeIndexBuilder extends InvertedIndexBuilder { 
	
	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

	/** Reference to our thread safe index */
	private final ThreadSafeIndex index;
	
	/** Number of threads to use*/
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
	 * Adds tasks to WorkQueue
	 * @author ryandielhenn
	 */
	private static class TaskMaster {
		
		private final List<Path> paths;
		
		private final WorkQueue tasks;
		
		private final ThreadSafeIndex index;
		
		private int pending;
		
		/**
		 * @param paths
		 * @param index2 
		 * @param threads 
		 */
		private TaskMaster(List<Path> paths, ThreadSafeIndex index, int threads) {
			this.paths = paths;
			this.tasks = new WorkQueue(threads);
			this.index = index;
			this.pending = 0;
		}
		
		/**
		 * @param paths
		 */
		private void start() {
			for (Path path : this.paths) {
				tasks.execute(new Task(path));
			}
			
		}
		
		/**
		 * @author ryandielhenn
		 *
		 */
		private class Task implements Runnable {
			
			/**
			 * 
			 */
			private final Path path;
			
			/**
			 * @param path
			 */
			public Task(Path path) {
				this.path = path;
				incrementPending();
				log.debug("Task for {} created.", path);
			}
			
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
		
		private synchronized void join() throws InterruptedException {
			while (this.pending > 0) {
				this.wait();
				log.debug("Woke up with pending at {}.", pending);				
			}
			log.debug("Work finished.");
		}
		
		
		private synchronized void incrementPending() {
			this.pending++;
		}
		
		private synchronized void decrementPending() {
			assert this.pending > 0;
			this.pending--;
			
			if (pending == 0) {
				this.notifyAll();
			}
		}
	}

}