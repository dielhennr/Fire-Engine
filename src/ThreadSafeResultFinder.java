import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
public class ThreadSafeResultFinder implements ResultFinderInterface {

	/** Index to search */
	private final ThreadSafeIndex index;

	/**
	 * Queries mapped to search results found from search
	 */
	private final TreeMap<String, List<SearchResult>> queryMap;

	/** Work Queue */
	private final WorkQueue workers;

	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

	/**
	 * Constructor
	 * 
	 * @param index   - Reference to our index
	 * @param workers - Reference to worker queue
	 */
	public ThreadSafeResultFinder(ThreadSafeIndex index, WorkQueue workers) {
		this.index = index;
		this.workers = workers;
		this.queryMap = new TreeMap<String, List<SearchResult>>();
	}

	/**
	 * Main thread creates a new TaskMaster, starts it, and then waits for all work
	 * to be finished
	 * 
	 * @param exact     - exact or partial search
	 * @param queryFile - File of queries to parse and search for.
	 */
	@Override
	public void parseQueries(Path queryFile, boolean exact) throws IOException {
		try {
			this.start(queryFile, exact);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		try {
			this.workers.join();
		} catch (InterruptedException e) {
			log.catching(Level.DEBUG, e);
		}
	}

	/**
	 * Fills our WorkQueue with runnable tasks, in this case a Task is searching for
	 * a line from the queryFile and adding the results to a queryMap
	 * 
	 * @param queryFile
	 * @param exact
	 * @throws IOException
	 */
	private void start(Path queryFile, boolean exact) throws IOException {
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
	 * Write our search results to an outputfile
	 * 
	 * @param outputFile
	 * @throws IOException
	 */
	public void writeResults(Path outputFile) throws IOException {
		synchronized (queryMap) {
			PrettyJSONWriter.asResultObject(queryMap, outputFile);
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
		 * @param line  - Query line to search for
		 * @param exact - Exact or partial search
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
			log.debug("Task for {} created.", line);
		}

		/**
		 * Carries out the work and then decrements TaskMaster's pending work In this
		 * case our work is to stem a query line, search for it in out index and put the
		 * results into our queryMap
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

				List<SearchResult> results = index.search(words, exact);
				synchronized (queryMap) {
					queryMap.put(query, results);
				}
			}
		}

	}
}