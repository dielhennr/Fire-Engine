import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will allow our main thread to keep track of pending work and wait
 * until all work is finished
 * 
 * @author Ryan Dielhenn
 */
public abstract class WorkTracker {

	// TODO Integrate pending into WorkQueue
	
	/** Logger to use */
	private final Logger log = LogManager.getLogger();

	/** Pending work */
	private int pending;

	/**
	 * Default constructor
	 */
	public WorkTracker() {
		this.pending = 0;
	}

	/**
	 * Wait until we have no more work
	 * 
	 * @throws InterruptedException
	 */
	protected synchronized void join() throws InterruptedException {
		while (this.pending > 0) {
			this.wait();
			log.debug("Woke up with pending at {}.", pending);
		}
		log.debug("Work finished.");
	}

	/**
	 * Increment pending work
	 */
	protected synchronized void incrementPending() {
		this.pending++;
	}

	/**
	 * Decrement pending work
	 */
	protected synchronized void decrementPending() {
		assert this.pending > 0;
		this.pending--;
		/**
		 * If we have no more work, notify to wake up from join. Our threads will be
		 * calling this method after completing a task
		 */
		if (pending == 0) {
			this.notifyAll();
		}
	}
}