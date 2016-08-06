package encryptor.encryptor.algorithms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.DecryptionApplier;
import encryptor.encryptor.EncryptionApplier;
import encryptor.encryptor.interfaces.Applier;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "EncryptionAlgorithm")
public abstract class EncryptionAlgorithm {
	
	
	public abstract byte encrypt(byte value, Key key);
	public abstract byte decrypt(byte value, Key key);
	public abstract boolean isValidKey(Key key);
	public abstract Key generateKey();
	
	public void decrypt(InputStream is,OutputStream os, Key key) throws IOException {
		doAction(is, os, new DecryptionApplier(this, key));
	}
	
	public void encrypt(InputStream is,OutputStream os,Key key) throws IOException {
		doAction(is, os, new EncryptionApplier(this, key));
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
}
