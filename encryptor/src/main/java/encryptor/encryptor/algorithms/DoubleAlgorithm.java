package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.algorithms.appliers.ActionApplier;
import encryptor.encryptor.algorithms.appliers.ApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name="DoubleAlgorithm")
@ToString(includeFieldNames = true)
public class DoubleAlgorithm extends EncryptionAlgorithm {
	
	@Getter @Setter
	private EncryptionAlgorithm firstAlgorithm;
	
	@Getter @Setter
	private EncryptionAlgorithm secondAlgorithm;

	public DoubleAlgorithm() {
		firstAlgorithm = secondAlgorithm = null;
	}
	
	public DoubleAlgorithm(EncryptionAlgorithm firstAlg, EncryptionAlgorithm secondAlg) {
		firstAlgorithm=firstAlg;
		secondAlgorithm=secondAlg;
	}
	
	@Inject
	public DoubleAlgorithm(
			@Named("encryptionApplierFactory")String encAppliercn,
			@Named("decryptionApplierFactory")String decAppliercn,
			EncryptionAlgorithm firstAlg, EncryptionAlgorithm secondAlg) {
		super(encAppliercn,decAppliercn);
		firstAlgorithm=firstAlg;
		secondAlgorithm=secondAlg;
	}

	@Override
	/**
	 * applies the fist nested algorithm (encryption) on the given value with the first sub key, and then applies the 
	 * second nested algorithm on the result of the first action with the second sub key.
	 */
	public byte encrypt(byte value, Key key) {
		Key firstKey = ((CompositeKey)key).getFirstKey();
		byte firstValue = firstAlgorithm.encrypt(value, firstKey);
		Key secondKey = ((CompositeKey)key).getSecondKey();
		return secondAlgorithm.encrypt(firstValue, secondKey);
	}

	@Override
	/**
	 * applies the fist nested algorithm (decryption) on the given value with the first sub key, and then applies the 
	 * second nested algorithm on the result of the first action with the second sub key.
	 */
	public byte decrypt(byte value, Key key) {
		Key secondKey = ((CompositeKey)key).getSecondKey();
		byte firstValue = secondAlgorithm.decrypt(value, secondKey);
		Key firstKey = ((CompositeKey)key).getFirstKey();
		return firstAlgorithm.decrypt(firstValue, firstKey);
	}

	@Override
	/**
	 * returns true if the given key is of type CompositeKey and both
	 * nested algorithms consider their respective sub keys valid.
	 */
	public boolean isValidKey(Key key) {
		if(!(key instanceof CompositeKey))
			return false;
		CompositeKey ck = (CompositeKey)key;
		return firstAlgorithm.isValidKey(ck.getFirstKey()) &&
				secondAlgorithm.isValidKey(ck.getSecondKey());
	}

	@Override
	/**
	 * generates a CompositeKey consisting of valid keys for the first and second algorithms.
	 */
	public Key generateKey() {
		return new CompositeKey(firstAlgorithm.generateKey(), secondAlgorithm.generateKey());
	}

}
