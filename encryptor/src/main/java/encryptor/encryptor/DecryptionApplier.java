package encryptor.encryptor;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;

public class DecryptionApplier implements Applier<Byte, Byte> {

	private EncryptionAlgorithm m_algorithm;
	private byte key;
	public DecryptionApplier(EncryptionAlgorithm alg,byte key) {
		this.key=key;
		this.m_algorithm=alg;
	}
	
	public Byte apply(Byte val) {
		return m_algorithm.decrypt(val, key);
	}

}
