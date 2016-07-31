package encryptor.encryptor.algorithms;

import encryptor.encryptor.Key;
import encryptor.encryptor.SingleValueKey;

public class xorEncryptionAlgorithm extends EncryptionAlgorithm {

	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return xor(value,valueOfKey);
	}

	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return xor(value,valueOfKey);
	}
	
	private byte xor(byte val1,byte val2) {
		return (byte) (val1 ^ val2);
	}

	public boolean isValidKey(Key key) {
		return true;
	}

}
