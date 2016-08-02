package encryptor.encryptor.algorithms;

import encryptor.encryptor.Key;

public class ReverseAlgorithm extends EncryptionAlgorithm {

	private EncryptionAlgorithm nestedAlgorithm;
	
	public ReverseAlgorithm(EncryptionAlgorithm nested) {
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
}
