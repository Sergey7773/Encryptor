package encryptor.encryptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import com.google.inject.Guice;

import dependencyInjection.DefaultStopwatchModule;
import encryptor.encryptor.interfaces.Observer;

public class ActionObserver implements Observer {
	
	private String onActionStartMessage;
	private String onActionEndMessage;
	private Stopwatch stopwatch;
	
	public ActionObserver(String onStartMessage, String onEndMessage) {
		this.onActionStartMessage = onStartMessage;
		this.onActionEndMessage = onEndMessage;
		stopwatch = Guice.createInjector(new DefaultStopwatchModule()).getInstance(Stopwatch.class);
	}
	
	/**
	 * prints the 'onStartMessage' which was passed as an argument to the constructor.
	 */
	public void onStart() {
		System.out.println(onActionStartMessage);
		stopwatch.start();
	}

	/**
	 * prints the 'onEndMessage' which was passed as an argument to the constructor, and the
	 * time in seconds the action took.
	 */
	public void onEnd() {
		System.out.println(onActionEndMessage);
		System.out.println("The action took "+
				stopwatch.getElapsedTimeInSeconds() + " seconds");
	}
}
