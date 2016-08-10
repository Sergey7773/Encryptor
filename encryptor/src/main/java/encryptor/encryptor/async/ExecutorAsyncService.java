package encryptor.encryptor.async;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class ExecutorAsyncService<T,S> {

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
	
	public ExecutorAsyncService() {
		threadPool = Executors.newFixedThreadPool(TOTAL_NUM_THREADS);
	}

	/**
	 * Performs reading and writing jobs, until all the tasks are complete.
	 * @param initialReadJobs 
	 * @param writeJobFactory - creates a writing job given a reading job.
	 * @param performerFactory - performs a writing job and returns the next reading job which is needed
	 * for this task, if no such reading job exists returns null
	 * @throws FileNotFoundException
	 */
	public void execute(
			Collection<S> initialReadJobs,
			WriteJobFactory<T, S> writeJobFactory,
			WriteJobPerformerFactory<S, T> performerFactory)  {
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
		/**
		 * reads from reading jobs queue as long as all tasks are not finished, transforms 
		 * reading jobs into writing jobs and pushes them into writing jobs queue.
		 */
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
		/**
		 * reads jobs from the writing queue and executes them as long as all tasks
		 * are not finished. creates reading jobs from the writing jobs and pushes them into the
		 * reading job queue.
		 */
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
