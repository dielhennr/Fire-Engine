import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A thread safe ResultFinder
 * 
 * @author Ryan Dielhenn
 *
 */
public class ThreadSafeResultFinder extends ResultFinder {

	/** Index to search */
	private ThreadSafeIndex index;

	/** Work Queue */
	private WorkQueue workers;

	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

	/**
	 * @param index
	 * @param workers
	 */
	public ThreadSafeResultFinder(InvertedIndex index, WorkQueue workers) {
		super(index);
		this.index = (ThreadSafeIndex) index;
		this.workers = workers;
	}

	@Override
	public void parseQueries(Path queryFile, boolean exact) throws IOException {
		TaskMaster master = new TaskMaster(this.workers, this.queryMap, this.index, queryFile, exact);
		try {
			master.start();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		try {
			master.join();
		} catch (InterruptedException e) {
			log.catching(Level.DEBUG, e);
		}
	}

	@Override
	public synchronized void writeResults(Path outputFile) throws IOException {
		super.writeResults(outputFile);
	}

	/**
	 * Adds tasks to WorkQueue and keeps track of pending work
	 *
	 * @see WorkTracker
	 * @author Ryan Dielhenn
	 */
	private static class TaskMaster extends WorkTracker {

		/** Our WorkQueue of threads */
		private final WorkQueue workers;

		/** Queries mapped to search results found from search */
		private final TreeMap<String, ArrayList<SearchResult>> queryMap;

		/** The Index to search */
		private final ThreadSafeIndex index;

		/** The file of queries to search for */
		private final Path queryFile;

		/** Exact or partial search */
		private final boolean exact;

		/**
		 * A Constructor for our task master
		 * 
		 * @param queryFile
		 * @param exact
		 * @param workers
		 * @param queryMap
		 * @param index
		 */
		private TaskMaster(WorkQueue workers, TreeMap<String, ArrayList<SearchResult>> queryMap, ThreadSafeIndex index,
				Path queryFile, boolean exact) {
			super();
			this.workers = workers;
			this.exact = exact;
			this.queryFile = queryFile;
			this.queryMap = queryMap;
			this.index = index;
		}

		/**
		 * Fills our WorkQueue with runnable tasks, in this case a Task is searching for
		 * a line from the queryFile and adding the results to a queryMap
		 * 
		 * @throws IOException
		 */
		private void start() throws IOException {
			try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
				String line;
				while ((line = reader.readLine()) != null) {
					workers.execute(new Task(line, exact));
				}

			} catch (IOException e) {
				throw e;
			}

		}

		/**
		 * A task class that represents a piece of work for a thread to carry out
		 */
		private class Task implements Runnable {
			/** The line to search for */
			private final String line;

			/** Exact or partial search */
			private final boolean exact;

			/**
			 * Constructor for the Task, initializes the path object and increments
			 * TaskMaster's pending work
			 * 
			 * @param line
			 * @param exact
			 */
			public Task(String line, boolean exact) {
				this.line = line;
				this.exact = exact;
				incrementPending();
				log.debug("Task for {} created.", line);
			}

			/**
			 * Carries out the work and then decrements TaskMaster's pending work
			 */
			@Override
			public void run() {
				/** Stem the query line and collect stemmed words into a set */
				Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
				TreeSet<String> words = TextFileStemmer.stemLineStream(line, stemmer)
						.collect(Collectors.toCollection(TreeSet::new));
				/** Add the query line and it's search results to the queryMap */
				if (!words.isEmpty()) {
					String query = String.join(" ", words);
					synchronized (queryMap) {
						queryMap.put(query, index.search(words, exact));
					}
				}
				decrementPending();
			}

		}
	}

}