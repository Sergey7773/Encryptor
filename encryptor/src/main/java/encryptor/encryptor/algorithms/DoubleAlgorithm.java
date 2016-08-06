package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name="DoubleAlgorithm")
public class DoubleAlgorithm extends EncryptionAlgorithm {
	
	@XmlElement
	private EncryptionAlgorithm firstAlgorithm;
	
	@XmlElement
	private EncryptionAlgorithm secondAlgorithm;

	public DoubleAlgorithm() {
		firstAlgorithm = secondAlgorithm = null;
	}
	
	public DoubleAlgorithm(EncryptionAlgorithm firstAlg, EncryptionAlgorithm secondAlg) {
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
