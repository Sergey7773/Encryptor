package encryptor.encryptor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import report.FailureReport;
import report.Report;
import report.Reports;
import report.SuccessReport;
import encryptor.encryptor.Main.Action;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Observer;
import encryptor.encryptor.xml.Utils;

public class EncryptionAlgorithmExecutor {

	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";

	private List<Observer> encryptionObservers;
	private List<Observer> decryptionObservers;
	
	@Inject
	@Named("ActionLogger")
	private Logger logger;

	public EncryptionAlgorithmExecutor() {
		encryptionObservers = new ArrayList<Observer>();
		decryptionObservers = new ArrayList<Observer>();

		encryptionObservers.add(new ActionObserver("Encryption started.",
				"Encryption ended."));
		decryptionObservers.add(new ActionObserver("Decryption started",
				"Decryption ended"));
	}
	
	public List<Observer> getEncryptionObservers() {
		return this.encryptionObservers;
	}
	
	public List<Observer> getDecryptionObservers() {
		return this.decryptionObservers;
	}

	//Sync execution
	public void executeEncyption(EncryptionAlgorithm algorithm,File inputFile) throws IOException {
		Key key = generateAndWriteKey(algorithm,inputFile);
		execute(algorithm, inputFile, key, Action.ENCRYPT);
	}

	public void executeDecryption(EncryptionAlgorithm algorithm,File inputFile, Key key) throws IOException {
		execute(algorithm, inputFile, key, Action.DECRYPT);
	}


	private void execute(EncryptionAlgorithm algorithm,File inputFile,
			Key key, Action actionType) throws IOException {
		List<Observer> toNotify = (actionType.equals(Action.ENCRYPT)) ?
				encryptionObservers : decryptionObservers;
		notifyObserversOnStart(toNotify);
		if(inputFile.isFile())
			performActionOnSingleFile(algorithm,inputFile,key,actionType);
		else 
			performActionOnDirectory(algorithm,inputFile, key, actionType);
		notifyObserversOnEnd(toNotify);
	}

	private void performActionOnSingleFile(EncryptionAlgorithm algorithm,File inputFile,
			Key key, Action actionType) 
					throws IOException {
		String outputFilePath = (actionType.equals(Action.ENCRYPT)) ?
				appedEncryptedToFilename(inputFile) : appedDecryptedToFilename(inputFile);
				FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
				FileInputStream fis = new FileInputStream(inputFile);
				logger.info(String.format("%s of %s started.",actionType,inputFile));
				try{
					performAction(algorithm, actionType, fis, fos, key);
					logger.info(String.format("%s of %s finished successfully.",actionType,inputFile));
				} catch(Exception e) {
					logger.info(String.format("%s of %s finished ended with the following exception %s:\n"
							+ "%s.",actionType,inputFile,e.getClass().getName()));
				}
	}

	private void performActionOnDirectory(EncryptionAlgorithm algorithm, File inputDir,
			Key key, Action actionType) 
					throws IOException {
		File outputDir = createOutputDirectory(inputDir, actionType);
		Reports reports = new Reports();
		List<Report> reportsList = reports.getReports();
		File[] filesInDir = inputDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}
		});
		for(int i=0;i<filesInDir.length;i++) {
			FileInputStream fis = new FileInputStream(filesInDir[i]);
			FileOutputStream fos = new FileOutputStream(
					new File(outputDir.getPath()+"/"+filesInDir[i].getName()));
			logger.info(String.format("%s of %s started.",actionType,filesInDir[i]));
			try{
				long startTime = System.currentTimeMillis();
				performAction(algorithm, actionType, fis, fos, key);
				SuccessReport sr = new SuccessReport();
				sr.setTime(999); //TODO: measure elapsed time
				reportsList.add(sr);
				logger.info(String.format("%s of %s finished successfully.",actionType,filesInDir[i]));
			} catch (Exception e) {
				FailureReport fr = new FailureReport();
				fr.setExceptionMessage(e.getMessage());
				fr.setExceptionName(e.getClass().getName());
				fr.setStackTrace(e.getStackTrace().toString());
				reportsList.add(fr);
				logger.info(String.format("%s of %s finished ended with the following exception %s:\n"
						+ "%s.",actionType,filesInDir[i],e.getClass().getName()));
			}
			
			Utils.marshallReports(reports, outputDir+"/reports.xml");
		}
	}


	//Async execution
	public void executeEncryptionAsync(EncryptionAlgorithm algorithm,File inputFile) throws IOException {
		Key key = generateAndWriteKey(algorithm, inputFile);
		executeAsync(algorithm,inputFile,key,Action.ENCRYPT);

	}

	public void executeDecryptionAsync(EncryptionAlgorithm algorithm,File inputFile, Key key) throws IOException {
		executeAsync(algorithm,inputFile,key,Action.DECRYPT);
	}

	private void executeAsync(EncryptionAlgorithm algorithm,File inputFile,
			Key key, Action actionType) throws IOException {
		List<Observer> toNotify = (actionType.equals(Action.ENCRYPT)) ?
				encryptionObservers : decryptionObservers;
		notifyObserversOnStart(toNotify);
		if(inputFile.isFile()) {
			performActionOnSingleFile(algorithm, inputFile, key, actionType);
		} else {
			performAsyncActionOnDirectory(algorithm,inputFile,key,actionType);
		}
		notifyObserversOnEnd(toNotify);
	}

	private void performAsyncActionOnDirectory(EncryptionAlgorithm algorithm, File inputDir,
			Key key, Action actionType) throws IOException {
		File outputDir = createOutputDirectory(inputDir, actionType);
		File[] filesInDir = inputDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}
		});

		new EncryptionExecutorAsyncService().execute(outputDir, filesInDir, algorithm, actionType, key);

	}

	//Utility functions
	private String appedEncryptedToFilename(File f) {
		return f.getPath()+ENCRYPTED_FORMAT;
	}

	private String appedDecryptedToFilename(File f) {
		String $ = f.getPath();
		$=$.replace(ENCRYPTED_FORMAT, "");
		int lastDot = $.lastIndexOf('.');
		if(lastDot==-1) return $+DECRYPTED_EXTENTION;
		$=$.substring(0, lastDot)+DECRYPTED_EXTENTION+$.substring(lastDot, $.length());
		return $;
	}

	private void performAction(EncryptionAlgorithm algorithm, Action actionType,
			FileInputStream fis, FileOutputStream fos,Key key) throws IOException {
		if(actionType.equals(Action.ENCRYPT)) {
			algorithm.encrypt(fis,fos,key);
		} else {
			algorithm.decrypt(fis,fos,key);
		}
	}

	private void notifyObserversOnStart(List<Observer> observers) {
		for(Observer observer : observers) 
			observer.onStart();
	}

	private void notifyObserversOnEnd(List<Observer> observers) {
		for(Observer observer : observers) 
			observer.onEnd();
	}

	private File createOutputDirectory(File inputDir, Action action) throws IOException {
		String outputDirPath = (action.equals(Action.ENCRYPT)) ?
				inputDir.getPath()+"/encrypted" : inputDir.getPath() + "/decrypted";
		File outputDir = new File(outputDirPath);
		outputDir.createNewFile();
		return outputDir;
	}

	private Key generateAndWriteKey(EncryptionAlgorithm alg, File inputFile) throws IOException {

		Key key = alg.generateKey();
		String keyPathfile = (inputFile.isDirectory()) ? inputFile.getPath()+"/encrypted/key.bin" 
				: inputFile.getParentFile().getPath()+"/key.bin";
		FileOutputStream fos = new FileOutputStream(keyPathfile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(key);
		oos.close();
		return key;
	}
}
