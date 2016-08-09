package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import reports.Report;
import encryptor.encryptor.Action;
import encryptor.encryptor.AsyncJob;
import encryptor.encryptor.Stopwatch;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;

public class LoggedWriteJobPerformerFactory implements WriteJobPerformerFactory<Pair<File,FileInputStream>,AsyncJob> {

	private ConcurrentHashMap<File,Stopwatch> fileActionTimers;
	private Action action;
	private EncryptionAlgorithm alg;
	private File outputDir;
	private Key key;
	private ConcurrentLinkedQueue<Report> reportsList;
	
	public LoggedWriteJobPerformerFactory(ConcurrentHashMap<File,Stopwatch> fileActionTimers, EncryptionAlgorithm algorithm,
			Action actionType,Key key,File outputDir,ConcurrentLinkedQueue<Report> reportsList) {
		this.fileActionTimers = fileActionTimers;
		this.action = actionType;
		this.alg = algorithm;
		this.key = key;
		this.reportsList = reportsList;
		this.outputDir = outputDir;
	}
	
	@Override
	public WriteJobPerformer<Pair<File, FileInputStream>, AsyncJob> get() {
		return new LoggedWriteJobPerformer(fileActionTimers, alg, action, key, outputDir, reportsList);
	}
	
}
