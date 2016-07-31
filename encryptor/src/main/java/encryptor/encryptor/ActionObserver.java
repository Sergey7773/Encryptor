package encryptor.encryptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class ActionObserver implements Observer {
	
	private String onActionStartMessage;
	private String onActionEndMessage;
	private Clock clock;
	private Instant startingInstant;
	
	public ActionObserver(String onStartMessage, String onEndMessage, Clock measuringClock) {
		this.onActionStartMessage = onStartMessage;
		this.onActionEndMessage = onEndMessage;
		clock = measuringClock;
	}
	
	public void onStart() {
		System.out.println(onActionStartMessage);
		startingInstant = clock.instant();
	}

	public void onEnd() {
		System.out.println(onActionEndMessage);
		System.out.println("The action took "+
				Duration.between(clock.instant(), startingInstant).toMillis()/1000 + " seconds");
	}
}
