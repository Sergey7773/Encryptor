package encryptor.encryptor.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import encryptor.encryptor.ActionObserver;
import encryptor.encryptor.Applier;
import encryptor.encryptor.DecryptionApplier;
import encryptor.encryptor.EncryptionApplier;
import encryptor.encryptor.MillisClock;
import encryptor.encryptor.Observer;

public abstract class EncryptionAlgorithm {
	
	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";
	
	private List<Observer> encryptionObservers;
	private List<Observer> decryptionObservers;
	
	public EncryptionAlgorithm() {
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
		doAction(f, outputFile, userOutputStream, new DecryptionApplier(this, key));
		notifyObserversOnEnd(decryptionObservers);
	}
	
	public abstract byte encrypt(byte value, byte key);
	public abstract byte decrypt(byte value, byte key);
	public abstract boolean isValidKey(byte key);
	
	public void encrypt(File f,OutputStream userOutputStream) throws IOException {
		byte[] key = new byte[1];
		new Random().nextBytes(key);
		userOutputStream.write(key);
		File outputFile = new File(appedEncryptedToFilename(f));
		notifyObserversOnStart(encryptionObservers);
		doAction(f, outputFile, userOutputStream, new EncryptionApplier(this, key[0]));
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
