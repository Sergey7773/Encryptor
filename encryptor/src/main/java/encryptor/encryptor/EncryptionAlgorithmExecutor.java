package encryptor.encryptor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import encryptor.encryptor.Main.Action;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;

public class EncryptionAlgorithmExecutor {

	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";


	protected List<Observer> encryptionObservers;
	protected List<Observer> decryptionObservers;

	public EncryptionAlgorithmExecutor() {
		encryptionObservers = new ArrayList<Observer>();
		decryptionObservers = new ArrayList<Observer>();

		encryptionObservers.add(new ActionObserver("Encryption started.",
				"Encryption ended.", new MillisClock()));
		decryptionObservers.add(new ActionObserver("Decryption started",
				"Decryption ended", new MillisClock()));
	}
	
	//Sync execution
	public void executeEncyption(EncryptionAlgorithm algorithm,File inputFile) throws IOException {
		Key key = algorithm.generateKey();
		//TODO: save key to file
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
				performAction(algorithm, actionType, fis, fos, key);
	}

	private void performActionOnDirectory(EncryptionAlgorithm algorithm, File inputDir,
			Key key, Action actionType) 
					throws IOException {
		File outputDir = createOutputDirectory(inputDir, actionType);
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
			performAction(algorithm, actionType, fis, fos, key);
		}
	}


	//Async execution
	public void executeEncryptionAsync(EncryptionAlgorithm algorithm,File inputFile) {
		Key key = algorithm.generateKey();
		//TODO: save key to file
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
	
	
}
