package encryptor.encryptor;

public class EncryptionApplier implements Applier<Byte,Byte> {

	private EncryptionAlgorithm m_algorithm;
	private byte key;
	public EncryptionApplier(EncryptionAlgorithm alg,byte key) {
		this.key=key;
		this.m_algorithm=alg;
	}
	
	public Byte apply(Byte val) {
		return m_algorithm.encrypt(val, key);
	}

}
