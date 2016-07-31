package encryptor.encryptor.algorithms;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.Key;

public class DoubleAlgorithm<T extends EncryptionAlgorithm, S extends EncryptionAlgorithm> 
	extends EncryptionAlgorithm {
	
	private T firstAlgorithm;
	private S secondAlgorithm;
	
	public DoubleAlgorithm(T firstAlg, S secondAlg) {
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
