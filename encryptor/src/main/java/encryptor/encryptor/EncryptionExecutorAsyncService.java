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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encryptor.encryptor.Main.Action;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;


public class EncryptionExecutorAsyncService {

	private static int NUM_READER_THREADS = 1;
	private static int TOTAL_NUM_THREADS = 4;
	private static int BUFFER_SIZE = 500;
	
	private BlockingQueue<Pair<InputStream,String>> readyStreams;
	private ConcurrentLinkedQueue<File> filesToProccess;
	private ExecutorService threadPool;
	private File outputDir;

	public EncryptionExecutorAsyncService() {
		threadPool = Executors.newFixedThreadPool(TOTAL_NUM_THREADS);
	}

	public void execute(File outputDir, File[] files, EncryptionAlgorithm algorithm, Action action,Key key) {
		filesToProccess = new ConcurrentLinkedQueue<File>();
		readyStreams = new ArrayBlockingQueue<Pair<InputStream,String>>(files.length);
		this.outputDir = outputDir;

		int readers = Math.min(NUM_READER_THREADS, TOTAL_NUM_THREADS);
		for(int i = 0;i < readers; i++) {
			threadPool.execute(new ReaderTask());
		}

		while(readyStreams.size()!=0) {
			Pair<InputStream,String> pair = readyStreams.poll();
			if(pair==null) break;
			threadPool.execute(new WriterTask(pair.first,pair.second,algorithm,action,key));
		}
	}

	private class ReaderTask implements Runnable {

		@Override
		public void run() {
			while(!filesToProccess.isEmpty()) {
				File file = filesToProccess.poll();
				if(file==null) break;
				try {
					FileInputStream fis = new FileInputStream(file);
					PipedInputStream pis = new PipedInputStream();
					PipedOutputStream pos = new PipedOutputStream(pis);
					byte[] buffer = new byte[BUFFER_SIZE];

					int offset=0;
					int toWrite=0;
					while(fis.available()>0) {
						toWrite = fis.read(buffer,offset,BUFFER_SIZE);
						pos.write(buffer,offset,toWrite);
						offset+=BUFFER_SIZE;
					}
					fis.close();
					pos.close();
					readyStreams.put(new Pair<InputStream,String>(pis,outputDir.getPath()+"/"+file.getName()));
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

		public WriterTask(InputStream in,String outputFilepath,
				EncryptionAlgorithm alg, Action action,Key key) {
			try {
				this.os=new FileOutputStream(new File(outputFilepath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.is=in;
			this.alg=alg;
			this.action=action;
			this.key = key;
		}

		@Override
		public void run() {
			try{
				if(action.equals(Action.ENCRYPT)) {
					alg.encrypt(is, os, key);
				} else {
					alg.decrypt(is, os, key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
