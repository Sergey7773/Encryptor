package dependencyInjection;

import java.time.Clock;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import encryptor.encryptor.MillisClock;

public class DefaultStopwatchModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(Clock.class)
		.annotatedWith(Names.named("StopWatchClock"))
		.to(MillisClock.class);
	}

}
