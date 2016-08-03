package encryptor.encryptor.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import encryptor.encryptor.ActionObserver;
import encryptor.encryptor.Applier;
import encryptor.encryptor.DecryptionApplier;
import encryptor.encryptor.EncryptionApplier;
import encryptor.encryptor.Key;
import encryptor.encryptor.MillisClock;
import encryptor.encryptor.Observer;
import encryptor.encryptor.SingleValueKey;

public abstract class EncryptionAlgorithm {
	
	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";
	
	protected List<Observer> encryptionObservers;
	protected List<Observer> decryptionObservers;
	
	public EncryptionAlgorithm() {
		encryptionObservers = new ArrayList<Observer>();
		decryptionObservers = new ArrayList<Observer>();
		
		encryptionObservers.add(new ActionObserver("Encryption started.",
				"Encryption ended.", new MillisClock()));
		decryptionObservers.add(new ActionObserver("Decryption started",
				"Decryption ended", new MillisClock()));
	}
	
	protected String appedEncryptedToFilename(File f) {
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
	
	public void decrypt(File f,OutputStream userOutputStream, Key key) throws IOException {
		File outputFile = new File(appedDecryptedToFilename(f));
		notifyObserversOnStart(decryptionObservers);
		doAction(f, outputFile, userOutputStream, new DecryptionApplier(this, key));
		notifyObserversOnEnd(decryptionObservers);
	}
	
	public abstract byte encrypt(byte value, Key key);
	public abstract byte decrypt(byte value, Key key);
	public abstract boolean isValidKey(Key key);
	
	public void encrypt(File f,OutputStream userOutputStream) throws IOException {
		SingleValueKey key = SingleValueKey.generate();
		File keyFile = new File("key.bin");
		if(!keyFile.exists()) {
			keyFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(keyFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(key);
		oos.close();
		
		File outputFile = new File(appedEncryptedToFilename(f));
		notifyObserversOnStart(encryptionObservers);
		doAction(f, outputFile, userOutputStream, new EncryptionApplier(this, key));
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
	
	protected void notifyObserversOnStart(List<Observer> observers) {
		for(Observer observer : observers) 
			observer.onStart();
	}
	
	protected void notifyObserversOnEnd(List<Observer> observers) {
		for(Observer observer : observers) 
			observer.onEnd();
	}
}
