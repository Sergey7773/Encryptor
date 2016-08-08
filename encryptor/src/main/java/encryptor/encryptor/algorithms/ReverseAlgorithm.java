package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "ReverseAlgorithm")
public class ReverseAlgorithm extends EncryptionAlgorithm {
	
	@XmlElement
	private EncryptionAlgorithm nestedAlgorithm;
	
	public ReverseAlgorithm() {
		this.nestedAlgorithm = null;
	}
	
	public ReverseAlgorithm(EncryptionAlgorithm nested) {
		this.nestedAlgorithm = nested;
	}
	
	@Inject
	public ReverseAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory,
			EncryptionAlgorithm nested) {
		super(encryptionApplierFactory,decryptionApplierFactory);
		this.nestedAlgorithm = nested;
	}
	
	
	@Override
	public byte encrypt(byte value, Key key) {
		return nestedAlgorithm.decrypt(value, key);
	}

	@Override
	public byte decrypt(byte value, Key key) {
		return nestedAlgorithm.encrypt(value, key);
	}

	@Override
	public boolean isValidKey(Key key) {
		return nestedAlgorithm.isValidKey(key);
	}

	@Override
	public Key generateKey() {
		return nestedAlgorithm.generateKey();
	}
}
