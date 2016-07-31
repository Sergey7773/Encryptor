package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlgorithmWrapper {
	
	private EncryptionAlgorithm m_encryptionAlgorithm;
	
	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";
	
	private List<Observer> encryptionObservers;
	private List<Observer> decryptionObservers;
	
	public AlgorithmWrapper(EncryptionAlgorithm algorithm) {
		m_encryptionAlgorithm = algorithm;
		encryptionObservers = new ArrayList<Observer>();
		decryptionObservers = new ArrayList<Observer>();
		
		encryptionObservers.add(new ActionObserver("Encryption started.",
				"Encryption ended.", new MillisClock()));
		decryptionObservers.add(new ActionObserver("Decryption started",
				"Decryption ended", new MillisClock()));
	}
	
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
	
	public void decrypt(File f,OutputStream userOutputStream, byte key) throws IOException {
		File outputFile = new File(appedDecryptedToFilename(f));
		notifyObserversOnStart(decryptionObservers);
		doAction(f, outputFile, userOutputStream, new DecryptionApplier(m_encryptionAlgorithm, key));
		notifyObserversOnEnd(decryptionObservers);
	}
	
	public void encrypt(File f,OutputStream userOutputStream) throws IOException {
		byte[] key = new byte[1];
		new Random().nextBytes(key);
		userOutputStream.write(key);
		File outputFile = new File(appedEncryptedToFilename(f));
		notifyObserversOnStart(encryptionObservers);
		doAction(f, outputFile, userOutputStream, new EncryptionApplier(m_encryptionAlgorithm, key[0]));
		notifyObserversOnEnd(encryptionObservers);
	}
	
	private void doAction(File f,File outputFile,OutputStream userOutputStream,Applier<Byte,Byte> function) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		byte plain[] = new byte[1];
		byte cyphered[] = new byte[1];
		while(fis.available()>0) {
			fis.read(plain);
			cyphered[0]=function.apply(plain[0]);
			fos.write(cyphered);
		}
		
		fis.close();
		fos.close();
	}
	
	private void notifyObserversOnStart(List<Observer> observers) {
		for(Observer observer : observers) 
			observer.onStart();
	}
	
	private void notifyObserversOnEnd(List<Observer> observers) {
		for(Observer observer : observers) 
			observer.onEnd();
	}
}
