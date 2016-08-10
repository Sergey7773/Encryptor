package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "ReverseAlgorithm")
@ToString( includeFieldNames = true)
public class ReverseAlgorithm extends EncryptionAlgorithm {
	
	@Getter @Setter
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
	/**
	 * calls the decryption function of the nested algorithm with the given arguments and returns the result.
	 */
	public byte encrypt(byte value, Key key) {
		return nestedAlgorithm.decrypt(value, key);
	}

	@Override
	/**
	 * calls the encryption function of the nested algorithm with the given arguments and returns the result.
	 */
	public byte decrypt(byte value, Key key) {
		return nestedAlgorithm.encrypt(value, key);
	}

	@Override
	/**
	 * returns true if and only if the given key is considered valid by the nested algorithm.
	 */
	public boolean isValidKey(Key key) {
		return nestedAlgorithm.isValidKey(key);
	}

	@Override
	/**
	 * calls generateKey() of the nested algorithm and returns the result.
	 */
	public Key generateKey() {
		return nestedAlgorithm.generateKey();
	}
}
