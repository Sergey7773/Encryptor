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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import dependencyInjection.DefaultEncryptionAlgorithmModule;
import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.appliers.ActionApplier;
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.algorithms.appliers.EncryptionApplier;
import encryptor.encryptor.algorithms.appliers.SplitDecryptionApplierFactory;
import encryptor.encryptor.algorithms.appliers.SplitEncryptionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "SplitAlgorithm")
@ToString(includeFieldNames = true)
public class SplitAlgorithm extends EncryptionAlgorithm {

	@Getter @Setter
	private EncryptionAlgorithm nestedAlgorithm;

	@Inject
	public SplitAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory,
			EncryptionAlgorithm nested) {
		super(encryptionApplierFactory,decryptionApplierFactory);
		this.nestedAlgorithm = nested;
	}

	public SplitAlgorithm() {
		super(new SplitEncryptionApplierFactory(), new SplitDecryptionApplierFactory());
		this.nestedAlgorithm = null;
	}

	public SplitAlgorithm(EncryptionAlgorithm nested) {
		super(new SplitEncryptionApplierFactory(), new SplitDecryptionApplierFactory());
		this.nestedAlgorithm = nested;
	}

	@Override
	/**
	 * if the byte has an even index in the stream which we read, we will apply the encryption
	 * function of the nested algorithm with the first sub key, otherwise we will apply the encryption
	 * function with the second sub key
	 */
	public byte encrypt(byte value, Key key) {
		return nestedAlgorithm.encrypt(value, key);
	}

	@Override
	/**
	 * if the byte has an even index in the stream which we read, we will apply the decryption
	 * function of the nested algorithm with the first sub key, otherwise we will apply the decryption
	 * function with the second sub key
	 */
	public byte decrypt(byte value, Key key) {
		return nestedAlgorithm.decrypt(value, key);
	}

	@Override
	/**
	 * returns true if the key is of type CompositeKey and both sub keys are 
	 * valid according to the nested algorithm
	 */
	public boolean isValidKey(Key key) {
		if(!(key instanceof CompositeKey)) return false;
		CompositeKey ck = ((CompositeKey)key);
		return nestedAlgorithm.isValidKey(ck.getFirstKey()) && nestedAlgorithm.isValidKey(ck.getSecondKey());
	}

	@Override
	/**
	 * generates a composite key which consists of two sub keys which are valid according to the nested algorithm.
	 */
	public Key generateKey() {
		return new CompositeKey(nestedAlgorithm.generateKey(), nestedAlgorithm.generateKey());
	}

}
