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
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
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
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory,
			EncryptionAlgorithm firstAlg, EncryptionAlgorithm secondAlg) {
		super(encryptionApplierFactory,decryptionApplierFactory);
		firstAlgorithm=firstAlg;
		secondAlgorithm=secondAlg;
	}

	@Override
	public byte encrypt(byte value, Key key) {
		Key firstKey = ((CompositeKey)key).getFirstKey();
		byte firstValue = firstAlgorithm.encrypt(value, firstKey);
		Key secondKey = ((CompositeKey)key).getSecondKey();
		return secondAlgorithm.encrypt(firstValue, secondKey);
	}

	@Override
	public byte decrypt(byte value, Key key) {
		Key secondKey = ((CompositeKey)key).getSecondKey();
		byte firstValue = secondAlgorithm.decrypt(value, secondKey);
		Key firstKey = ((CompositeKey)key).getFirstKey();
		return firstAlgorithm.decrypt(firstValue, firstKey);
	}

	@Override
	public boolean isValidKey(Key key) {
		if(!(key instanceof CompositeKey))
			return false;
		return true;
	}

	@Override
	public Key generateKey() {
		return new CompositeKey(firstAlgorithm.generateKey(), secondAlgorithm.generateKey());
	}

}
