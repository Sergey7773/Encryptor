package encryptor.encryptor.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.EncryptionApplier;
import encryptor.encryptor.Key;
import encryptor.encryptor.SingleValueKey;

public class SplitAlgorithm extends EncryptionAlgorithm {

	public EncryptionAlgorithm nestedAlgorithm;
	
	public SplitAlgorithm(EncryptionAlgorithm nested) {
		this.nestedAlgorithm = nested;
	}
	
	@Override
	public byte encrypt(byte value, Key key) {
		return nestedAlgorithm.encrypt(value, key);
	}

	@Override
	public byte decrypt(byte value, Key key) {
		return nestedAlgorithm.decrypt(value, key);
	}

	@Override
	public boolean isValidKey(Key key) {
		return nestedAlgorithm.isValidKey(key);
	}
	
	@Override
	public void encrypt(InputStream is,OutputStream os) throws IOException {
		SingleValueKey firstKey = SingleValueKey.generate();
		SingleValueKey secondKey = SingleValueKey.generate();
		CompositeKey composite = new CompositeKey(firstKey, secondKey);
		
		File keyFile = new File("key.bin");
		if(!keyFile.exists()) {
			keyFile.createNewFile();
		}
		FileOutputStream kfos = new FileOutputStream(keyFile);
		ObjectOutputStream oos = new ObjectOutputStream(kfos);
		oos.writeObject(composite);
		oos.close();
		
		notifyObserversOnStart(encryptionObservers);
		
		byte plain[] = new byte[1];
		byte cyphered[] = new byte[1];
		int counter = 0;
		SingleValueKey encKey;
		while(is.available()>0) {
			encKey = (counter % 2 ==0) ? firstKey : secondKey;
			cyphered[0] = this.encrypt(plain[0], encKey);
			os.write(cyphered);
			counter++;
		}
		
		
		notifyObserversOnEnd(encryptionObservers);
	}

}
