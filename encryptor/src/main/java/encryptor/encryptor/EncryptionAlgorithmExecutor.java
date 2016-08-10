package encryptor.encryptor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;




import lombok.Cleanup;
import lombok.NonNull;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import dependencyInjection.DefaultStopwatchModule;
import reports.FailureReport;
import reports.Report;
import reports.Reports;
import reports.SuccessReport;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.async.AsyncJob;
import encryptor.encryptor.async.ExecutorAsyncService;
import encryptor.encryptor.async.LoggedWriteJobFactory;
import encryptor.encryptor.async.LoggedWriteJobPerformerFactory;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Observer;
import encryptor.encryptor.interfaces.Pair;
import encryptor.encryptor.xml.XmlParser;

/**
 * A wrapper class for the execution of encryption\decryption algorithms.
 * Receives two lists of observers which will be executed before and after encryption\decryption,
 * if a directory is passed as an argument, these observers will be executed before encryption\decryption of any
 * files begin, and once encryption\decryption of all files has ended.
 * @author Sergey
 *
 */
public class EncryptionAlgorithmExecutor {

	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";

	private List<Observer> encryptionObservers;
	private List<Observer> decryptionObservers;
	private XmlParser xmlParser;

	@Inject
	public EncryptionAlgorithmExecutor(
			@NonNull @Named("encObservers")List<Observer> encryptionObservers,
			@NonNull @Named("decObservers")List<Observer> decryptionObservers,
			@NonNull XmlParser xmlParser) {
		this.encryptionObservers = encryptionObservers;
		this.decryptionObservers = decryptionObservers;
		this.xmlParser = xmlParser;
	}
	
	public EncryptionAlgorithmExecutor() {
		this(new ArrayList<Observer>(), new ArrayList<Observer>(),new XmlParser());
	}

	/**
	 * 
	 * @return the list of observers which will be called before and after encryption.
	 */
	public List<Observer> getEncryptionObservers() {
		return this.encryptionObservers;
	}

	/**
	 * 
	 * @return the list of observers which will be called before and after decryption.
	 */
	public List<Observer> getDecryptionObservers() {
		return this.decryptionObservers;
	}

	//Sync execution
	
	/**
	 * Performs encryption of the given file (or directory) on a single thread.
	 * @param algorithm - the used encryption algorithm
	 * @param inputFile - file or directory to encrypt.
	 * @throws IOException
	 */
	public void executeEncyption(@NonNull EncryptionAlgorithm algorithm,
			@NonNull File inputFile) throws IOException {
		Key key = algorithm.generateKey();
		execute(algorithm, inputFile, key, Action.ENCRYPT);
		writeKey(key,inputFile);
	}
	
	/**
	 * Performs decryption of the given file (or directory) on a single thread, with the given key.
	 * @param algorithm - the used encryption algorithm
	 * @param inputFile - file or directory to decrypt
	 * @param key - the decryption key
	 * @throws IOException
	 */
	public void executeDecryption(@NonNull EncryptionAlgorithm algorithm,
			@NonNull File inputFile, Key key) throws IOException {
		execute(algorithm, inputFile, key, Action.DECRYPT);
	}


	/**
	 * calls the observers according to the Action argument passed to the method, and 
	 * calls the matching private mehod for encrypting\decrypting a file or directory according the the
	 * File argument.
	 * @param algorithm
	 * @param inputFile
	 * @param key
	 * @param actionType
	 * @throws IOException
	 */
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

	/**
	 * performs the action on the inputFile and creates an output file to write the result.
	 * @param algorithm
	 * @param inputFile
	 * @param key
	 * @param actionType
	 * @throws IOException
	 */
	private void performActionOnSingleFile(EncryptionAlgorithm algorithm,File inputFile,
			Key key, Action actionType) 
					throws IOException {
		String outputFilePath = (actionType.equals(Action.ENCRYPT)) ?
				appedEncryptedToFilename(inputFile) : appedDecryptedToFilename(inputFile);
				performAction(algorithm, actionType, inputFile.getPath(), outputFilePath, key);
	}
	
	/**
	 * performs the action on every file in the input directory, and writes the output to a 
	 * 'decrypted'/'encrypted' sub directory according to the action which was received.
	 * @param algorithm
	 * @param inputDir
	 * @param key
	 * @param actionType
	 * @throws IOException
	 */
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
			reportsList.add(performAction(algorithm, actionType,
					inputDir.getPath()+"/"+filesInDir[i].getName(),
					outputDir.getPath()+"/"+filesInDir[i].getName(), key));
		}
		xmlParser.marshallReports(reports, inputDir+"/reports.xml");
	}


	//Async execution
	
	/**
	 * Performs encryption of the given of a directory on multiple threads, encryption of a file will be 
	 * carried out on a single thread.
	 * @param algorithm - the used encryption algorithm
	 * @param inputFile - file or directory to encrypt.
	 * @throws IOException
	 */
	public void executeEncryptionAsync(@NonNull EncryptionAlgorithm algorithm,
			@NonNull File inputFile) throws IOException {
		Key key = algorithm.generateKey();
		executeAsync(algorithm,inputFile,key,Action.ENCRYPT);
		writeKey(key, inputFile);
	}

	/**
	 * Performs decryption of the given of a directory on multiple threads, decryption of a file will be 
	 * carried out on a single thread.
	 * @param algorithm - the used encryption algorithm
	 * @param inputFile - file or directory to encrypt.
	 * @throws IOException
	 */
	public void executeDecryptionAsync(EncryptionAlgorithm algorithm,File inputFile, Key key) throws IOException {
		executeAsync(algorithm,inputFile,key,Action.DECRYPT);
	}

	/**
	 * calls the observers according to the received action, and calls the methods which will perform 
	 * the encryption\decryption.
	 * @param algorithm
	 * @param inputFile
	 * @param key
	 * @param actionType
	 * @throws IOException
	 */
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
	
	/**
	 * performs encryption\decryption of a given directory using multiple threads.
	 * @param algorithm
	 * @param inputDir
	 * @param key
	 * @param actionType
	 * @throws IOException
	 */
	private void performAsyncActionOnDirectory(EncryptionAlgorithm algorithm, File inputDir,
			Key key, Action actionType) throws IOException {
		File outputDir = createOutputDirectory(inputDir, actionType);
		File[] filesInDir = inputDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}
		});
		
		ConcurrentLinkedQueue<Report> reportsList = new ConcurrentLinkedQueue<Report>();
		ConcurrentHashMap<File, Stopwatch> fileActionTimers = new ConcurrentHashMap<File, Stopwatch>();
		
		ExecutorAsyncService<AsyncJob,Pair<File,FileInputStream>> service =
				new ExecutorAsyncService<AsyncJob,Pair<File,FileInputStream>>();
		List<Pair<File,FileInputStream>> initialReadJobs = new ArrayList<Pair<File,FileInputStream>>();
		Reports reports = new Reports();
		for(File f : filesInDir) {
			try {
				initialReadJobs.add(new Pair<File,FileInputStream>(f, new FileInputStream(f)));
			} catch(Exception e) {
				LoggingUtils.writeActionStart(getClass().getName(), actionType, f.getName());
				reports.getReports().add(new FailureReport(f.getName(),e));
				LoggingUtils.writeActionFinishedWithFailure(getClass().getName(), actionType, f.getName(), e);
			}
		}
		service.execute(initialReadJobs,
				new LoggedWriteJobFactory(fileActionTimers, actionType),
				new LoggedWriteJobPerformerFactory(
						fileActionTimers, algorithm, actionType, key, outputDir, reportsList));
		reports.getReports().addAll(reportsList);
		String reportsOutputDir = (actionType.equals(Action.ENCRYPT)) ? inputDir.getPath() : inputDir.getParent();
		reportsOutputDir = reportsOutputDir +"/reports.xml";
		xmlParser.marshallReports(reports,reportsOutputDir);
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

	/**
	 * calls the encryption algorithm which will perform the decryption\encryption of a file, 
	 * logs the start of the action and the ending of it (either success or failure) and returns 
	 * a report according to the result (success or failure).
	 * @param algorithm
	 * @param actionType
	 * @param inputFilepath
	 * @param outputFilepath
	 * @param key
	 * @return SuccessReport if the action completed without throwing an exception, FailureReport otherwise.
	 */
	private Report performAction(EncryptionAlgorithm algorithm, Action actionType,
			String inputFilepath, String outputFilepath, Key key) {
		Report report = null;
		try {
			@Cleanup FileInputStream fis = new FileInputStream(new File(inputFilepath));
			@Cleanup FileOutputStream fos = new FileOutputStream(new File(outputFilepath));
			
			LoggingUtils.writeActionStart(getClass().getName(), actionType, inputFilepath);
			Stopwatch sw = Guice.createInjector(new DefaultStopwatchModule()).getInstance(Stopwatch.class);
			
			sw.start();
			if(actionType.equals(Action.ENCRYPT)) {
				algorithm.encrypt(fis,fos,key);
			} else {
				algorithm.decrypt(fis,fos,key);
			}
			
			int elapsedTime = sw.getElapsedTimeInSeconds();
			LoggingUtils.writeActionFinisedWithSuccess(getClass().getName(),
					actionType, inputFilepath, elapsedTime);
			report = new SuccessReport(inputFilepath,sw.getElapsedTimeInSeconds());
		} catch(IOException e) {
			LoggingUtils.writeActionFinishedWithFailure(getClass().getName(), actionType, inputFilepath, e);
			report = new FailureReport(inputFilepath, e);
		}
		return report;
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
		String outputDirPath = "";
		if(action.equals(Action.ENCRYPT)) {
			outputDirPath = inputDir.getPath()+"/encrypted";
		} else {
			outputDirPath = inputDir.getParent()+"/decrypted";
		}

		File outputDir = new File(outputDirPath);
		outputDir.mkdir();
		return outputDir;
	}

	/**
	 * writes the key to a file name 'key.bin', which will be located in the same directory
	 * with the inputFile if inputFile is a single file, and in inputFile if inputFile is a directory.
	 * @param key
	 * @param inputFile
	 * @throws IOException
	 */
	private void writeKey(Key key, File inputFile) throws IOException {

		String keyPathfile = (inputFile.isDirectory()) ? inputFile.getPath()+"/key.bin" 
				: inputFile.getParentFile().getPath()+"/key.bin";
		FileOutputStream fos = new FileOutputStream(keyPathfile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(key);
		oos.close();
	}
}
