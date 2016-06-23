package encryptor.encryptor;

public class xorEncryptionAlgorithm implements EncryptionAlgorithm {

	public byte encrypt(byte value, byte key) {
		return xor(value,key);
	}

	public byte decrypt(byte value, byte key) {
		return xor(value,key);
	}
	
	private byte xor(byte val1,byte val2) {
		return (byte) (val1 ^ val2);
	}

}
