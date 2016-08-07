package encryptor.encryptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Stopwatch {
	
	private Instant startingInstant;
	
	private Clock clock;
	
	@Inject
	public Stopwatch(@Named("StopWatchClock") Clock clock) {
		this.clock=clock;
	}
	
	public Instant start() {
		startingInstant = clock.instant();
		return startingInstant;
	}
	
	public int getElapsedTimeInSeconds() {
		return (int) (getElapsedTimeInMillis()/1000);
	}
	
	public long getElapsedTimeInMillis() {
		return Duration.between(startingInstant,clock.instant()).toMillis();
	}
}
