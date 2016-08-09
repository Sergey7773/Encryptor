package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;

import com.google.inject.Guice;

import dependencyInjection.DefaultStopwatchModule;
import encryptor.encryptor.Action;
import encryptor.encryptor.LoggingUtils;
import encryptor.encryptor.Stopwatch;
import encryptor.encryptor.interfaces.Pair;

@AllArgsConstructor
public class LoggedWriteJobFactory implements WriteJobFactory<AsyncJob, Pair<File,FileInputStream>>{

	private ConcurrentHashMap<File,Stopwatch> fileActionTimers;
	private Action actionType;

	private static int BUFFER_SIZE = 1000;

	@Override
	public AsyncJob make(Pair<File, FileInputStream> readJob) {
		try {
			File file = readJob.getFirst();
			FileInputStream fis = readJob.getSecond();
			if(fileActionTimers.get(file)==null) {
				Stopwatch sw = Guice.createInjector(new DefaultStopwatchModule()).getInstance(Stopwatch.class);
				sw.start();
				fileActionTimers.put(file, sw);
				LoggingUtils.writeActionStart(getClass().getName(), actionType, file.getPath());
			}

			PipedInputStream pis = new PipedInputStream();
			PipedOutputStream pos = new PipedOutputStream(pis);
			byte[] buffer = new byte[BUFFER_SIZE];
			int toWrite=0;
			toWrite = fis.read(buffer,0,BUFFER_SIZE);
			if(toWrite>-1)
				pos.write(buffer,0,toWrite);
			pos.close(); 
			return new AsyncJob(file,fis,pis);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
