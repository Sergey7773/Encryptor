package encryptor.encryptor.algorithms;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.Key;

public class DoubleAlgorithm extends EncryptionAlgorithm {
	
	private EncryptionAlgorithm firstAlgorithm;
	private EncryptionAlgorithm secondAlgorithm;
	
	public DoubleAlgorithm(EncryptionAlgorithm firstAlg, EncryptionAlgorithm secondAlg) {
		firstAlgorithm=firstAlg;
		secondAlgorithm=secondAlg;
	}

	@Override
	public byte encrypt(byte value, Key key) {
		Key firstKey = ((CompositeKey)key).getFirstKey();
		byte firstValue = firstAlgorithm.encrypt(value, firstKey);
		Key secondKey = ((CompositeKey)key).getSeconKey();
		return secondAlgorithm.encrypt(firstValue, secondKey);
	}

	@Override
	public byte decrypt(byte value, Key key) {
		Key secondKey = ((CompositeKey)key).getSeconKey();
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

}
