package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.google.inject.Guice;

import dependencyInjection.DefaultStopwatchModule;
import reports.FailureReport;
import reports.Reports;
import reports.SuccessReport;
import encryptor.encryptor.Action;
import encryptor.encryptor.LoggingUtils;
import encryptor.encryptor.Stopwatch;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;
import encryptor.encryptor.xml.Utils;


public class EncryptionExecutorAsyncService<T,S> {

	private static int NUM_READER_THREADS = 1;
	private static int TOTAL_NUM_THREADS = 8;
	private static int JOB_QUEUE_SIZE = 100;

	private BlockingQueue<T> writeJobQueue;
	private BlockingQueue<S> readJobsQueue;
	private AtomicInteger tasksToFinish;
	private ExecutorService threadPool;

	private Lock lock;
	private Condition readyToRead;
	private Condition readyToWrite;
	
	public EncryptionExecutorAsyncService() {
		threadPool = Executors.newFixedThreadPool(TOTAL_NUM_THREADS);
	}

	public void execute(
			Key key,Collection<S> initialReadJobs,
			WriteJobFactory<T, S> writeJobFactory,
			WriteJobPerformerFactory<S, T> performerFactory) throws FileNotFoundException {
		readJobsQueue = new ArrayBlockingQueue<S>(JOB_QUEUE_SIZE);
		readJobsQueue.addAll(initialReadJobs);
		tasksToFinish =	new AtomicInteger(initialReadJobs.size());
		writeJobQueue = new ArrayBlockingQueue<T>(JOB_QUEUE_SIZE);
		
		lock = new ReentrantLock();
		readyToRead = lock.newCondition();
		readyToWrite = lock.newCondition();

		int readers = NUM_READER_THREADS;
		for(int i = 0;i < readers; i++) {
			threadPool.execute(new ReaderTask<T,S>(writeJobFactory,readJobsQueue,writeJobQueue));
		}

		for(int i=readers;i<TOTAL_NUM_THREADS;i++) {
			threadPool.execute(new WriterTask<T,S>(readJobsQueue,writeJobQueue,performerFactory.get()));
		}

		threadPool.shutdown();
		while(!threadPool.isTerminated()) {
			try {
				threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class ReaderTask<WriteJob,ReadJob> implements Runnable {
		
		private WriteJobFactory<WriteJob,ReadJob> factory;
		private BlockingQueue<ReadJob> readQueue;
		private BlockingQueue<WriteJob> writeQueue;
		
		public ReaderTask(WriteJobFactory<WriteJob,ReadJob> factory,
				BlockingQueue<ReadJob> readQueue,
				BlockingQueue<WriteJob> writeQueue) {
			this.factory = factory;
			this.readQueue = readQueue;
			this.writeQueue = writeQueue;
		}
		
		private void wakeupAll(boolean aquireLock) {
			if(aquireLock)
				lock.lock();
			readyToRead.signalAll();
			readyToWrite.signalAll();
			lock.unlock();
		}
		
		private ReadJob getNextJob() throws InterruptedException {
			lock.lock();
			ReadJob pair = readQueue.poll();
			while(pair==null) {
				if(tasksToFinish.get()==0) {
					wakeupAll(false);
					return null;
				}
				readyToRead.await();
				pair = readQueue.poll();
			}
			lock.unlock();
			return pair;
		}
		
		@Override
		public void run() {
			while(tasksToFinish.get()>0) {
				try {		
					ReadJob readJob = getNextJob();
					if(readJob == null) return;
					
					WriteJob writeJob = factory.make(readJob);
					
					lock.lock();
					writeQueue.put(writeJob);
					readyToWrite.signal();
					lock.unlock();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class WriterTask<WriteJob,ReadJob> implements Runnable {
		
		private BlockingQueue<ReadJob> readQueue;
		private BlockingQueue<WriteJob> writeQueue;
		private WriteJobPerformer<ReadJob,WriteJob> performer;

		public WriterTask(BlockingQueue<ReadJob> readQueue, BlockingQueue<WriteJob> writeQueue,
				WriteJobPerformer<ReadJob,WriteJob> performer) {
			this.readQueue = readQueue;
			this.writeQueue = writeQueue;
			this.performer = performer;
		}
		
		private void sendReadyToReadSignal() {
			lock.lock();
			readyToRead.signal();
			lock.unlock();
		}
		
		private void wakeupAll(boolean aquireLock) {
			if(aquireLock)
				lock.lock();
			readyToRead.signalAll();
			readyToWrite.signalAll();
			lock.unlock();
		}
		
		private WriteJob getNextWriteJob() {
			lock.lock();
			WriteJob job = writeQueue.poll();
			while(job == null) {
				try {
					readyToWrite.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(tasksToFinish.get()==0) {
					wakeupAll(false);
					return null;
				} 
				job = writeQueue.poll();
			}
			lock.unlock();
			return job;
		}

		@Override
		public void run() {
			
			while(tasksToFinish.get()>0) {
				WriteJob job = getNextWriteJob();
				if(job==null) return;
				try {
					
					ReadJob nextReadJob = performer.perform(job);
					if(nextReadJob == null) {
						if(tasksToFinish.decrementAndGet()==0){
							wakeupAll(true);
						}
					} else {
						readQueue.offer(nextReadJob);
						sendReadyToReadSignal();
					}

				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}

	}
}
