package encryptor.encryptor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import encryptor.encryptor.async.ExecutorAsyncService;
import encryptor.encryptor.async.WriteJobFactory;
import encryptor.encryptor.async.WriteJobPerformer;
import encryptor.encryptor.async.WriteJobPerformerFactory;

public class ExecutorAsyncServiceTest {
	
	private ExecutorAsyncService<Integer, Integer> $;
	private WriteJobFactory<Integer, Integer> writeJobFactory;
	private WriteJobPerformerFactory<Integer,Integer> writeJobPerformerFactory;
	private Collection<Integer> initialJobs;
	private AtomicInteger readingJobsCounter;
	private AtomicInteger writingJobsCounter;
	
	@Before
	public void setup() {
		writeJobFactory = new WriteJobFactory<Integer, Integer>() {

			@Override
			public Integer make(Integer readJob) {
				readingJobsCounter.incrementAndGet();
				return readJob;
			}
		};
		
		writeJobPerformerFactory = new WriteJobPerformerFactory<Integer, Integer>() {

			@Override
			public WriteJobPerformer<Integer, Integer> get() {
				return new WriteJobPerformer<Integer, Integer>() {

					@Override
					public Integer perform(Integer s) {
						writingJobsCounter.incrementAndGet();
						if(s.intValue()==0)
							return null;
						return s-1;
					}
				};
			}
		};
		
		initialJobs = new ArrayList<Integer>();
		initialJobs.addAll(Arrays.asList(100,50,25,15,250,32));
		writingJobsCounter = new AtomicInteger(0);
		readingJobsCounter = new AtomicInteger(0);
		$ = new ExecutorAsyncService<Integer,Integer>();
	}
	
	@Test
	public void numberOfWritingJobsPerformedEqualsToNumberOfWritingJobsProduced() {
		$.execute(initialJobs, writeJobFactory, writeJobPerformerFactory);
		assertTrue(writingJobsCounter.intValue()==readingJobsCounter.intValue());
	}
	
}
