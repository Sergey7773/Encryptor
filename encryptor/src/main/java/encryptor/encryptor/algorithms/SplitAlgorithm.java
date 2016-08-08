package encryptor.encryptor.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.EncryptionApplier;
import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.interfaces.Applier;
import encryptor.encryptor.interfaces.BiApplier;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "SplitAlgorithm")
public class SplitAlgorithm extends EncryptionAlgorithm {

	@XmlElement
	private EncryptionAlgorithm nestedAlgorithm;
	
	public SplitAlgorithm() {
		this.nestedAlgorithm = null;
	}
	
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
	public void encrypt(InputStream is,OutputStream os,Key key) throws IOException {
		doAction(is, os, key, new BiApplier<Byte, Byte, Key>() {

			@Override
			public Byte apply(Byte t, Key u) {
				return encrypt(t,u);
			}
		});
	}
	
	@Override
	public void decrypt(InputStream is,OutputStream os,Key key) throws IOException {
		doAction(is, os, key, new BiApplier<Byte, Byte, Key>() {

			public Byte apply(Byte t, Key u) {
				return decrypt(t,u);
			}
		});
	}
	
	private void doAction(InputStream is,OutputStream os,Key key,
			BiApplier<Byte, Byte, Key> biApplier) throws IOException {
		if(!isValidKey(key)) {
			throw new IllegalArgumentException();
		}
		CompositeKey composite = (CompositeKey)key;
		byte in[] = new byte[500];
		byte out[] = new byte[500];
		int counter = 0;
		Key currKey;
		int read = 0;
		while(is.available()>0) {
			read = is.read(in);
			currKey = (counter % 2 ==0) ? composite.getFirstKey() : composite.getSecondKey();
			for(int i=0;i<500;i++) {
				out[i] = biApplier.apply(in[i], currKey);
			}
			os.write(out,0,read);
			counter++;
		}
	}

	@Override
	public Key generateKey() {
		return new CompositeKey(nestedAlgorithm.generateKey(), nestedAlgorithm.generateKey());
	}

}
