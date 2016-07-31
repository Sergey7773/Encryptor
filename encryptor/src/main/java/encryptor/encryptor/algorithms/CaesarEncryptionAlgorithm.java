package encryptor.encryptor.algorithms;

import encryptor.encryptor.Key;
import encryptor.encryptor.SingleValueKey;

public class CaesarEncryptionAlgorithm extends EncryptionAlgorithm {

	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		int delta = Byte.MAX_VALUE-value-valueOfKey;
		if(delta<0) {
			return (byte)(Byte.MIN_VALUE-delta);
		} 
		return (byte)(value+valueOfKey);
	}

	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		int delta = value-valueOfKey-Byte.MIN_VALUE;
		if(delta<0) {
			return (byte)(Byte.MAX_VALUE+delta);
		}
		return (byte)(value-valueOfKey);
	}

	public boolean isValidKey(Key key) {
		return true;
	}

}
