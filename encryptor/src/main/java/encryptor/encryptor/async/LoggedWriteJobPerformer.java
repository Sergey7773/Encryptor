package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.AllArgsConstructor;
import reports.FailureReport;
import reports.Report;
import reports.SuccessReport;
import encryptor.encryptor.Action;
import encryptor.encryptor.LoggingUtils;
import encryptor.encryptor.Stopwatch;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;

@AllArgsConstructor
public class LoggedWriteJobPerformer implements WriteJobPerformer<Pair<File,FileInputStream>,AsyncJob> {

	private ConcurrentHashMap<File,Stopwatch> fileActionTimers;
	private EncryptionAlgorithm alg;
	private Action action;
	private Key key;
	private File outputDir;
	private ConcurrentLinkedQueue<Report> reportsList;

	@Override
	public Pair<File, FileInputStream> perform(AsyncJob writeJob) {
		InputStream is = writeJob.getInputStream();
		File sourceFile = writeJob.getFile();
		FileInputStream fis = writeJob.getFileInputStream();
		try {
			FileOutputStream os = new FileOutputStream(
					outputDir.getPath()+"/"+writeJob.getFile().getName(),true);
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
				reportsList.add(new SuccessReport(sourceFile.getPath(), elapsedTime));
				fis.close();
				return null;
			} else {
				return new Pair<File,FileInputStream>(sourceFile,fis);
			} 
		} catch(Exception e) {
			e.printStackTrace();
			reportsList.add(new FailureReport(sourceFile.getPath(), e));
			LoggingUtils.writeActionFinishedWithFailure(getClass().getName(),
					action, sourceFile.getPath(), e);
		}
		return null;

	}


}
