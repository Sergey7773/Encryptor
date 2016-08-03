package encryptor.encryptor.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	
	
	public void decrypt(InputStream is,OutputStream os, Key key) throws IOException {
		notifyObserversOnStart(decryptionObservers);
		doAction(is, os, new DecryptionApplier(this, key));
		notifyObserversOnEnd(decryptionObservers);
	}
	
	public abstract byte encrypt(byte value, Key key);
	public abstract byte decrypt(byte value, Key key);
	public abstract boolean isValidKey(Key key);
	
	public void encrypt(InputStream inputFile,OutputStream outputFile) throws IOException {
		SingleValueKey key = SingleValueKey.generate();
		File keyFile = new File("key.bin");
		if(!keyFile.exists()) {
			keyFile.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(keyFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(key);
		oos.close();
		
		notifyObserversOnStart(encryptionObservers);
		doAction(inputFile, outputFile, new EncryptionApplier(this, key));
		notifyObserversOnEnd(encryptionObservers);
	}
	
	private void doAction(InputStream is,OutputStream os,Applier<Byte,Byte> function) throws IOException {
		byte plain[] = new byte[1];
		byte cyphered[] = new byte[1];
		while(is.available()>0) {
			is.read(plain);
			cyphered[0]=function.apply(plain[0]);
			os.write(cyphered);
		}
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
