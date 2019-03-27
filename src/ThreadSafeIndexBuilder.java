import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A thread safe InvertedIndexBuilder
 * @author Ryan Dielhenn
 *
 */
public class ThreadSafeIndexBuilder extends InvertedIndexBuilder {

	/**
	 * @param index
	 */
	public ThreadSafeIndexBuilder(InvertedIndex index) {
		super(index);
	}

	@Override
	public synchronized void build(List<Path> files) throws IOException {
		super.build(files);
	}

	@Override
	public synchronized void build(Path start) throws IOException {
		super.build(start);
	}
	

}