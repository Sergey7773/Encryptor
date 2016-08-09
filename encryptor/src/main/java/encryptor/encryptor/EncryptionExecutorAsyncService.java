package encryptor.encryptor;

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
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;
import encryptor.encryptor.xml.Utils;


public class EncryptionExecutorAsyncService {

	private static int NUM_READER_THREADS = 1;
	private static int TOTAL_NUM_THREADS = 8;
	private static int JOB_QUEUE_SIZE = 100;
	private static int BUFFER_SIZE = 1000;

	private BlockingQueue<AsyncJob> readyJobs;
	private ArrayBlockingQueue<Pair<File,FileInputStream>> fileInputStreams;
	private AtomicInteger filesToFinish;
	private ConcurrentHashMap<File, Stopwatch> fileActionTimers;
	private ExecutorService threadPool;
	private File outputDir;
	private Reports reportsList;
	private Action action;

	private Lock lock;
	private Condition readyToRead;
	private Condition readyToWrite;
	
	public EncryptionExecutorAsyncService() {
		threadPool = Executors.newFixedThreadPool(TOTAL_NUM_THREADS);
	}

	public void execute(File outputDir, File[] files, EncryptionAlgorithm algorithm, Action action,Key key) throws FileNotFoundException {
		fileInputStreams = new ArrayBlockingQueue<Pair<File,FileInputStream>>(JOB_QUEUE_SIZE);
		for(File f : files) {
			fileInputStreams.add(new Pair<File,FileInputStream>(f,new FileInputStream(f)));
		}
		filesToFinish =	new AtomicInteger(files.length);
		fileActionTimers = new ConcurrentHashMap<File,Stopwatch>();
		readyJobs = new ArrayBlockingQueue<AsyncJob>(JOB_QUEUE_SIZE);
		this.outputDir = outputDir;
		reportsList = new Reports();
		
		lock = new ReentrantLock();
		readyToRead = lock.newCondition();
		readyToWrite = lock.newCondition();
		
		
		this.action = action;

		int readers = NUM_READER_THREADS;
		for(int i = 0;i < readers; i++) {
			threadPool.execute(new ReaderTask());
		}

		for(int i=readers;i<TOTAL_NUM_THREADS;i++) {
			threadPool.execute(new WriterTask(algorithm,action,key));
		}
		
		
		threadPool.shutdown();
		while(!threadPool.isTerminated()) {
			try {
				threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Utils.marshallReports(reportsList, outputDir.getParent()+"/reports.xml");
	}

	private class ReaderTask implements Runnable {

		@Override
		public void run() {
			while(filesToFinish.get()>0) {
				try {		
					lock.lock();
					Pair<File,FileInputStream> pair = fileInputStreams.poll();
					while(pair==null) {
						if(filesToFinish.get()==0) {
							readyToRead.signalAll();
							readyToWrite.signalAll();
							lock.unlock();
							return;
						}
						readyToRead.await();
						pair = fileInputStreams.poll();
					}
					lock.unlock();
					FileInputStream fis = pair.second;
					File file = pair.first;
					
					if(fileActionTimers.get(file)==null) {
						Stopwatch sw = Guice.createInjector(new DefaultStopwatchModule()).getInstance(Stopwatch.class);
						sw.start();
						fileActionTimers.put(file, sw);
						LoggingUtils.writeActionStart(getClass().getName(), action, file.getPath());
					}
					
					PipedInputStream pis = new PipedInputStream();
					PipedOutputStream pos = new PipedOutputStream(pis);
					byte[] buffer = new byte[BUFFER_SIZE];
					int toWrite=0;
					toWrite = fis.read(buffer,0,BUFFER_SIZE);
					if(toWrite>-1)
						pos.write(buffer,0,toWrite);
					pos.close();
					lock.lock();
					readyJobs.put(new AsyncJob(file, fis, pis));
					readyToWrite.signal();
					lock.unlock();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private class WriterTask implements Runnable {

		private OutputStream os;
		private InputStream is;
		private EncryptionAlgorithm alg;
		private Action action;
		private Key key;
		private File sourceFile;
		private FileInputStream fis;

		public WriterTask(EncryptionAlgorithm alg, Action action,Key key) {
			this.alg = alg;
			this.key = key;
			this.action = action;
		}
		
		private void parseJob(AsyncJob job) {
			try {
				this.os=new FileOutputStream(
						new File(outputDir.getPath()+"/"+job.getFile().getName()),true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.is=job.getIs();
			this.sourceFile = job.getFile();
			this.fis = job.getFis();
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

		@Override
		public void run() {
			
			while(filesToFinish.get()>0) {
				lock.lock();
				AsyncJob job = readyJobs.poll();
				while(job == null) {
					try {
						readyToWrite.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(filesToFinish.get()==0) {
						wakeupAll(false);
						return;
					} 
					job = readyJobs.poll();
				}
				lock.unlock();
				try {
					
					parseJob(job);
					if(action.equals(Action.ENCRYPT)) {
						alg.encrypt(is, os, key);
					} else {
						alg.decrypt(is, os, key);
					}
					os.close();
					is.close();
					if(fis.available()==0) {
						int elapsedTime = fileActionTimers.get(sourceFile).getElapsedTimeInSeconds();
						LoggingUtils.writeActionFinisedWithSuccess(getClass().getName(),
								action, sourceFile.getPath(), elapsedTime);
						reportsList.getReports().add(new SuccessReport(sourceFile.getPath(), elapsedTime));
						if(filesToFinish.decrementAndGet()==0){
							wakeupAll(true);
						} 
						fis.close();
					} else {
						fileInputStreams.offer(new Pair<File,FileInputStream>(sourceFile,fis));
						sendReadyToReadSignal();
					} 
					sendReadyToReadSignal();
				} catch(Exception e) {
					reportsList.getReports().add(new FailureReport(sourceFile.getPath(), e));
					LoggingUtils.writeActionFinishedWithFailure(getClass().getName(),
							action, sourceFile.getPath(), e);
					if(filesToFinish.decrementAndGet()==0) {
						wakeupAll(true);
					} else {
						sendReadyToReadSignal();
					}
					try {
						fis.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		}

	}
}
