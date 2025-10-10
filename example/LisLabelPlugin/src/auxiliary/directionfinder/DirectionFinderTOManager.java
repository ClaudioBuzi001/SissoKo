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
package auxiliary.directionfinder;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import com.gifork.blackboard.BlackBoardUtility;
import com.leonardo.infrastructure.Pair;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class DirectionFinderTOManager.
 *
 * @author BOTTICCIM
 */
public class DirectionFinderTOManager implements Runnable {

	/** The clock. */
	private final Clock clock = Clock.systemDefaultZone();

	/** The timeout map. */
	private final ConcurrentHashMap<String, Pair<Long, Long>> timeoutMap = new ConcurrentHashMap<>();

	/**
	 * Constructor of Direction Finder Timeout Manager.
	 */
	public DirectionFinderTOManager() {
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		while (true) {
			checkTimeout();
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set a new Timer.
	 *
	 * @param timerId      the timer id
	 * @param timeoutValue the timeout value
	 */
	public void setTimer(final String timerId, final Long timeoutValue) {

		final long milliSecondsNow = clock.millis();
		timeoutMap.put(timerId, new Pair<>(milliSecondsNow, timeoutValue));

	}

	/**
	 * Delete a timer.
	 *
	 * @param timerId the timer id
	 */
	public void deleteTimer(final String timerId) {
		timeoutMap.remove(timerId);
	}

	/**
	 * Checking for timeout.
	 */
	private void checkTimeout() {
		timeoutMap.forEach((key, value) -> {
			final long millisecondsNow = clock.millis();
			if (millisecondsNow - value.getX() > value.getY()) {
				
				BlackBoardUtility.removeData(DataType.DIRECTION_FINDER.name(), key);
				
			}
		});
	}

}
