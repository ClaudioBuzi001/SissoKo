/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 *
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 *
 */
package application4F;

import com.fourflight.WP.ECI.WpCommon.WpLogger;
import com.fourflight.tech.perfo.TraceBean;
import com.leonardo.infrastructure.ArgumentManager;

/**
 * {@code CounterSp} is a singleton utility class used for simple performance monitoring.
 * It provides nanosecond-level timing functions to measure and log the duration of operations.
 * <p>
 * Timing information can be recorded and reported using the {@link TraceBean} class.
 * </p>
 */
public class CounterSp {

	/** The PERFO_METER. */
	public static final boolean PERFO_METER = ArgumentManager.getBooleanOf("perfoMeter");//false;
	
	/** The start Nanoseconds. */
	private long startNanoseconds;

	/**
	 * The Class SingletonLoader.
	 */
	private static class SingletonLoader {
		/** The instance. */
		private static final CounterSp INSTANCE = new CounterSp();
	}

	/**
	 * Gets the single instance of CounterSp.
	 *
	 * @return single instance of CounterSp
	 */
	public static CounterSp getInstance() {
		return SingletonLoader.INSTANCE;
	}

	 /**
     * Returns the elapsed time in nanoseconds since {@code setStartNanoSeconds()} was called.
     *
     * @return the elapsed time in nanoseconds
     */
	public long getElapsedTimeNanoSeconds() {
		return System.nanoTime() - startNanoseconds;
	}

	 /**
     * Sets the starting timestamp in nanoseconds.
     * <p>This timestamp is typically used as a baseline to calculate elapsed time for an operation.</p>
     *
     * @param startNanoseconds the start time in nanoseconds
     */
	public void setStartNanoSeconds(long startNanoseconds) {
		if (PERFO_METER) {
			this.startNanoseconds = startNanoseconds;			
		}
	}

	/**
	 * Records the elapsed time in milliseconds for performance monitoring purposes.
	 *
	 * <p>This method calculates the time elapsed since the last recorded timestamp 
	 * (typically using {@code getElapsedTimeNanoSeconds()}) and logs it through a 
	 * {@link TraceBean} object. The time is stored in milliseconds and is 
	 * associated with the provided label.</p>
	 *
	 * @param label a label used to identify the context or source of the timing data.
	 */
	public void printNanoSeconds(String label) {
		if (PERFO_METER) {
			double time = getElapsedTimeNanoSeconds() / 1000000.0;
			TraceBean t = new TraceBean(); 
			t.setCategory("metric");
			t.setResourceName("metric");
			t.addOrderData("wp.milliTime", String.valueOf(time));
			t.addOrderData("wp.items", label);			
			WpLogger.getInstance().log_PERFO_write(t);
		}
	}
}
