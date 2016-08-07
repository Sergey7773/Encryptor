package encryptor.encryptor;

import java.time.Clock;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class DefaultEncryptorInjector extends AbstractModule{

	@Override
	protected void configure() {
		bind(Clock.class).annotatedWith(Names.named("StopWatchClock")).to(MillisClock.class);
		bind(Logger.class)
		.annotatedWith(Names.named("ActionLogger"))
		.toInstance(new TimeKeepingLogger(Main.class.getName()));
	}

}