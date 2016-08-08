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
public class SplitAlgorithm extends EncryptionAlgorithm {

	@XmlElement
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
	public Key generateKey() {
		return new CompositeKey(nestedAlgorithm.generateKey(), nestedAlgorithm.generateKey());
	}

}
