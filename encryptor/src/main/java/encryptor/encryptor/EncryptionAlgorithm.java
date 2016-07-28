package encryptor.encryptor;

public interface EncryptionAlgorithm {
	public byte encrypt(byte value, byte key);
	public byte decrypt(byte value, byte key);
	public boolean isValidKey(byte key);
}
