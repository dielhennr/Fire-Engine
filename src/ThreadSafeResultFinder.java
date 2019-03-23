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
	

}