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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.inject.Guice;

import reports.FailureReport;
import reports.Reports;
import reports.SuccessReport;
import encryptor.encryptor.Main.Action;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;
import encryptor.encryptor.xml.Utils;


public class EncryptionExecutorAsyncService {

	private static int NUM_READER_THREADS = 1;
	private static int TOTAL_NUM_THREADS = 4;
	private static int BUFFER_SIZE = 500;

	private AtomicBoolean finished;
	private BlockingQueue<AsyncJob> readyJobs;
	private ConcurrentLinkedQueue<Pair<File,FileInputStream>> fileInputStreams;
	private ConcurrentLinkedQueue<File> filesToFinish;
	private ConcurrentHashMap<File, Stopwatch> fileActionTimers;
	private ExecutorService threadPool;
	private File outputDir;
	private Reports reportsList;

	private Lock lock;
	private Condition readyToRead;

	public EncryptionExecutorAsyncService() {
		threadPool = Executors.newFixedThreadPool(TOTAL_NUM_THREADS);
	}

	public void execute(File outputDir, File[] files, EncryptionAlgorithm algorithm, Action action,Key key) throws FileNotFoundException {
		fileInputStreams = new ConcurrentLinkedQueue<Pair<File,FileInputStream>>();
		for(File f : files) {
			fileInputStreams.add(new Pair<File,FileInputStream>(f,new FileInputStream(f)));
		}
		filesToFinish = new ConcurrentLinkedQueue<>(Arrays.asList(files));
		fileActionTimers = new ConcurrentHashMap<File,Stopwatch>();
		readyJobs = new ArrayBlockingQueue<AsyncJob>(files.length);
		this.outputDir = outputDir;
		finished = new AtomicBoolean(false);
		reportsList = new Reports();
		
		lock = new ReentrantLock();
		readyToRead = lock.newCondition();

		int readers = Math.min(NUM_READER_THREADS, TOTAL_NUM_THREADS);
		for(int i = 0;i < readers; i++) {
			threadPool.execute(new ReaderTask());
		}
		while(!finished.get()) {
			AsyncJob job = readyJobs.poll();
			while(job==null){
				if(finished.get()) {
					threadPool.shutdown();
					return;
				}
				job = readyJobs.poll();
			}
			threadPool.execute(new WriterTask(job,algorithm,action,key));
		}
		threadPool.shutdown();
		Utils.marshallReports(reportsList, outputDir.getParent()+"/reports.xml");
	}

	private class ReaderTask implements Runnable {

		@Override
		public void run() {
			while(!filesToFinish.isEmpty()) {
				try {		
					lock.lock();
					Pair<File,FileInputStream> pair = fileInputStreams.poll();
					while(pair==null) {
						if(finished.get() || filesToFinish.isEmpty()) {
							lock.unlock();
							return;
						}
						readyToRead.await();
						pair = fileInputStreams.poll();
					}
					lock.unlock();
					FileInputStream fis = pair.second;
					File file = pair.first;
					
					if(fileActionTimers.get(file)!=null) {
						Stopwatch sw = Guice.createInjector(new DefaultEncryptorInjector()).getInstance(Stopwatch.class);
						sw.start();
						fileActionTimers.put(file, sw);
						//TODO: write to log
					}
					
					PipedInputStream pis = new PipedInputStream();
					PipedOutputStream pos = new PipedOutputStream(pis);
					byte[] buffer = new byte[BUFFER_SIZE];
					int toWrite=0;
					toWrite = fis.read(buffer,0,BUFFER_SIZE);
					if(toWrite>0)
						pos.write(buffer,0,toWrite);
					pos.close();
					readyJobs.put(new AsyncJob(file, fis, pis));
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

		public WriterTask(AsyncJob job,
				EncryptionAlgorithm alg, Action action,Key key) {
			try {
				this.os=new FileOutputStream(
						new File(outputDir.getPath()+"/"+job.getFile().getName()),true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.is=job.getIs();
			this.alg=alg;
			this.action=action;
			this.key = key;
			this.sourceFile = job.getFile();
			this.fis = job.getFis();
		}

		@Override
		public void run() {
			try{
				if(action.equals(Action.ENCRYPT)) {
					alg.encrypt(is, os, key);
				} else {
					alg.decrypt(is, os, key);
				}
				os.close();
				is.close();
				if(fis.available()==0) {
					SuccessReport sr = new SuccessReport();
					sr.setTime(fileActionTimers.get(sourceFile).getElapsedTimeInSeconds());
					//TODO: write to log
					reportsList.getReports().add(sr);
					filesToFinish.remove(sourceFile);
					if(filesToFinish.isEmpty()) finished.set(true);
					fis.close();
				} else {
					fileInputStreams.offer(new Pair<File,FileInputStream>(sourceFile,fis));
				}
				lock.lock();
				readyToRead.signal();
				lock.unlock();
			} catch (IOException e) {
				
				FailureReport fr = new FailureReport();
				fr.setExceptionMessage(e.getMessage());
				fr.setExceptionName(e.getClass().getName());
				fr.setStackTrace(e.getStackTrace().toString());
				reportsList.getReports().add(fr);
				//TODO: write to log
				e.printStackTrace();
				filesToFinish.remove(sourceFile);
				if(filesToFinish.isEmpty()) finished.set(true);
				try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 

		}

	}
}
