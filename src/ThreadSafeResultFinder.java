import java.io.IOException;
import java.nio.file.Path;

/**
 * A thread safe ResultFinder
 * 
 * @author Ryan Dielhenn
 *
 */
public class ThreadSafeResultFinder extends ResultFinder {

	/**
	 * @param index
	 */
	public ThreadSafeResultFinder(InvertedIndex index) {
		super(index);
	}

	@Override
	public void parseQueries(Path queryFile, boolean exact) throws IOException {
		super.parseQueries(queryFile, exact);
	}

	@Override
	public synchronized void addQuery(String line, boolean exact) {
		super.addQuery(line, exact);
	}

	@Override
	public synchronized void writeResults(Path outputFile) throws IOException {
		super.writeResults(outputFile);
	}

}