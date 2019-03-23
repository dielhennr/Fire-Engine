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

}