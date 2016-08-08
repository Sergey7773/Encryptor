package encryptor.encryptor.algorithms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.algorithms.appliers.ActionApplier;
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.algorithms.appliers.DecryptionApplier;
import encryptor.encryptor.algorithms.appliers.DecryptionApplierFactory;
import encryptor.encryptor.algorithms.appliers.EncryptionApplier;
import encryptor.encryptor.algorithms.appliers.EncryptionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "EncryptionAlgorithm")
public abstract class EncryptionAlgorithm {
	
	
	public abstract byte encrypt(byte value, Key key);
	public abstract byte decrypt(byte value, Key key);
	public abstract boolean isValidKey(Key key);
	public abstract Key generateKey();
	
	@XmlElement
	private ActionApplierFactory encApplierFactory;
	@XmlElement
	private ActionApplierFactory decApplierFactory;
	
	public EncryptionAlgorithm() {
		encApplierFactory = new EncryptionApplierFactory();
		decApplierFactory = new DecryptionApplierFactory();
	}
	
	@Inject
	public EncryptionAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory) {
		this.encApplierFactory = encryptionApplierFactory;
		this.decApplierFactory = decryptionApplierFactory;
	}
	
	public void decrypt(InputStream is,OutputStream os, Key key) throws IOException {
		doAction(is, os,decApplierFactory.get(this), key);
	}
	
	public void encrypt(InputStream is,OutputStream os,Key key) throws IOException {
		doAction(is, os, encApplierFactory.get(this), key);
	}
	
	private void doAction(InputStream is,OutputStream os,ActionApplier function,Key key) throws IOException {
		byte plain[] = new byte[500];
		byte cyphered[] = new byte[500];
		int read = 0;
		while(is.available()>0) {
			read = is.read(plain);
			for(int i=0;i<500;i++)
				cyphered[i]=function.apply(plain[i],key);
			os.write(cyphered,0,read);
		}
	}
}
