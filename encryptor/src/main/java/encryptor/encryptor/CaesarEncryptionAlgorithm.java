package encryptor.encryptor;

public class CaesarEncryptionAlgorithm implements EncryptionAlgorithm {

	public byte encrypt(byte value, byte key) {
		int delta = Byte.MAX_VALUE-value-key;
		if(delta<0) {
			return (byte)(Byte.MIN_VALUE-delta);
		} 
		return (byte)(value+key);
	}

	public byte decrypt(byte value, byte key) {
		int delta = value-key-Byte.MIN_VALUE;
		if(delta<0) {
			return (byte)(Byte.MAX_VALUE+delta);
		}
		return (byte)(value-key);
	}

}
