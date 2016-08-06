package encryptor.encryptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Stopwatch {
	
	private Instant startingInstant;
	
	@Inject
	@Named("StopWatchClock")
	private Clock clock;
	
	public Instant start() {
		startingInstant = clock.instant();
		return startingInstant;
	}
	
	public int getElapsedTimeInSeconds() {
		return (int) (getElapsedTimeInMillis()/1000);
	}
	
	public long getElapsedTimeInMillis() {
		return Duration.between(clock.instant(), startingInstant).toMillis();
	}
}
