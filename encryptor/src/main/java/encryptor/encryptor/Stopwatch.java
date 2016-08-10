package encryptor.encryptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * A utility class to measure time from a certain instant.
 * @author Sergey
 *
 */
public class Stopwatch {
	
	private Instant startingInstant;
	
	private Clock clock;
	
	@Inject
	public Stopwatch(@Named("StopWatchClock") Clock clock) {
		this.clock=clock;
	}
	
	/**
	 * gets the current instant from the clock which was used to instantiate this object and returns it.
	 * all measurements of this object in the future will be relative to this instant (unless we call start again).
	 * @return
	 */
	public Instant start() {
		startingInstant = clock.instant();
		return startingInstant;
	}
	
	/**
	 *
	 * @return the time which passed from the last call to 'start' in seconds.
	 */
	public int getElapsedTimeInSeconds() {
		return (int) (getElapsedTimeInMillis()/1000);
	}
	
	/**
	 * 
	 * @return the time which passed from the last call to 'start' in milliseconds
	 */
	public long getElapsedTimeInMillis() {
		return Duration.between(startingInstant,clock.instant()).toMillis();
	}
}
