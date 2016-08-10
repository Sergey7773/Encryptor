package encryptor.encryptor;

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

public class StopwatchTest {
	
	private Stopwatch $;
	private Clock mockClock;
	
	@Before
	public void setup() {
		AbstractModule testModule = new AbstractModule() {
			
			@Override
			protected void configure() {
				mockClock=Mockito.mock(Clock.class);
				bind(Clock.class).annotatedWith(Names.named("StopWatchClock")).toInstance(mockClock);
			}
		};
		
		$ = Guice.createInjector(testModule).getInstance(Stopwatch.class);
	}
	
	@Test
	public void returnsCurrentInstantOnStart() {
		Instant ins = Instant.ofEpochSecond(1000);
		Mockito.doReturn(ins).when(mockClock).instant();
		assertEquals(ins,$.start());
	}
	
	@Test
	public void returnsCorrectElapsedTimeInSeconds() {
		Mockito.doReturn(Instant.ofEpochSecond(1000)).when(mockClock).instant();
		$.start();
		Mockito.doReturn(Instant.ofEpochSecond(2000)).when(mockClock).instant();
		assertEquals(1000,$.getElapsedTimeInSeconds());
	}
	
	@Test
	public void returnsCorrectElapsedTimeInMilliseconds() {
		Mockito.doReturn(Instant.ofEpochMilli(1000)).when(mockClock).instant();
		$.start();
		Mockito.doReturn(Instant.ofEpochMilli(3000)).when(mockClock).instant();
		assertEquals(2000,$.getElapsedTimeInMillis());
	}
	
}
