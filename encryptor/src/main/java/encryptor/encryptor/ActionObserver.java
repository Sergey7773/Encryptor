package encryptor.encryptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import com.google.inject.Guice;

import encryptor.encryptor.interfaces.Observer;

public class ActionObserver implements Observer {
	
	private String onActionStartMessage;
	private String onActionEndMessage;
	private Stopwatch stopwatch;
	
	public ActionObserver(String onStartMessage, String onEndMessage) {
		this.onActionStartMessage = onStartMessage;
		this.onActionEndMessage = onEndMessage;
		stopwatch = Guice.createInjector(new DefaultEncryptorInjector()).getInstance(Stopwatch.class);
	}
	
	public void onStart() {
		System.out.println(onActionStartMessage);
		stopwatch.start();
	}

	public void onEnd() {
		System.out.println(onActionEndMessage);
		System.out.println("The action took "+
				stopwatch.getElapsedTimeInSeconds() + " seconds");
	}
}
