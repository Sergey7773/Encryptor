package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.AllArgsConstructor;
import reports.Report;
import encryptor.encryptor.Action;
import encryptor.encryptor.Stopwatch;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Pair;

@AllArgsConstructor
public class LoggedWriteJobPerformerFactory implements WriteJobPerformerFactory<Pair<File,FileInputStream>,AsyncJob> {

	private ConcurrentHashMap<File,Stopwatch> fileActionTimers;
	private EncryptionAlgorithm alg;
	private Action action;
	private Key key;
	private File outputDir;
	private ConcurrentLinkedQueue<Report> reportsList;
	
	@Override
	public WriteJobPerformer<Pair<File, FileInputStream>, AsyncJob> get() {
		return new LoggedWriteJobPerformer(fileActionTimers, alg, action, key, outputDir, reportsList);
	}
	
}
