package encryptor.encryptor.algorithms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.algorithms.appliers.ActionApplier;
import encryptor.encryptor.algorithms.appliers.ApplierFactory;
import encryptor.encryptor.algorithms.appliers.DecryptionApplier;
import encryptor.encryptor.algorithms.appliers.EncryptionApplier;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "EncryptionAlgorithm")
@ToString(exclude = {"encApplierFactory","decApplierFactory"})
public abstract class EncryptionAlgorithm {
	
	/**
	 * encrypts a single byte with the given key, if the key is legal, and returns the result.
	 * @param value
	 * @param key
	 * @return
	 */
	public abstract byte encrypt(byte value, Key key);
	/**
	 * decrypts a single byte with the given key, if the key is legal, and returns the result.
	 * @param value
	 * @param key
	 * @return
	 */
	public abstract byte decrypt(byte value, Key key);
	/**
	 * 
	 * @param key
	 * @return true if the key is legal for this algoritm and false otherwise
	 */
	public abstract boolean isValidKey(Key key);
	/**
	 * 
	 * @return a Key which is legal for this algorithm.
	 */
	public abstract Key generateKey();
	
	@XmlTransient
	private ApplierFactory<? extends ActionApplier> encApplierFactory;
	@XmlTransient
	private ApplierFactory<? extends ActionApplier> decApplierFactory;

	private String encApplierClassName;
	private String decApplierClassName;
	
	@Inject
	public EncryptionAlgorithm(
			@Named("encryptionApplierFactory")String encAppliercn,
			@Named("decryptionApplierFactory")String decAppliercn) {
		encApplierClassName = encAppliercn;
		decApplierClassName = decAppliercn;
		encApplierFactory = loadApplierFactory(encAppliercn);
		decApplierFactory = loadApplierFactory(decAppliercn);
	}
	
	public EncryptionAlgorithm() {
		encApplierClassName = EncryptionApplier.class.getName();
		decApplierClassName = DecryptionApplier.class.getName();
		encApplierFactory = new ApplierFactory<EncryptionApplier>(EncryptionApplier.class);
		decApplierFactory = new ApplierFactory<DecryptionApplier>(DecryptionApplier.class);
	}

	
	/**
	 * reads from the given InputStream as long as possible, and decrypts every byte in the stream
	 * by calling the decrypt(byte value,Key key) function.
	 * @param is
	 * @param os
	 * @param key
	 * @throws IOException
	 */
	public void decrypt(InputStream is,OutputStream os, Key key) throws IOException {
		doAction(is, os,decApplierFactory.get(this), key);
	}
	
	/**
	 * reads from the given InputStream as long as possible, and encrypts every byte in the stream
	 * by calling the encrypts(byte value,Key key) function.
	 * @param is
	 * @param os
	 * @param key
	 * @throws IOException
	 */
	public void encrypt(InputStream is,OutputStream os,Key key) throws IOException {
		doAction(is, os, encApplierFactory.get(this), key);
	}
	
	/**
	 * reads from the given InputStream and writes to the OutputStream, while applying the ActionApplier function
	 * on each byte.
	 * @param is
	 * @param os
	 * @param function
	 * @param key
	 * @throws IOException
	 */
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
	public String getEncApplierClassName() {
		return encApplierClassName;
	}
	public void setEncApplierClassName(String encApplierClassName) {
		this.encApplierClassName = encApplierClassName;
	}
	public String getDecApplierClassName() {
		return decApplierClassName;
	}
	public void setDecApplierClassName(String decApplierClassName) {
		this.decApplierClassName = decApplierClassName;
	}
	
	private ApplierFactory loadApplierFactory(String className) {
		Class<ActionApplier> clz;
		try {
			clz = (Class<ActionApplier>) ClassLoader.
					getSystemClassLoader().loadClass(EncryptionApplier.class.getName());
			return new ApplierFactory<ActionApplier>(clz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
