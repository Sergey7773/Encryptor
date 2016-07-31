package encryptor.encryptor;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;

public class DecryptionApplier implements Applier<Byte, Byte> {

	private EncryptionAlgorithm m_algorithm;
	private Key key;
	public DecryptionApplier(EncryptionAlgorithm alg,Key key) {
		this.key=key;
		this.m_algorithm=alg;
	}
	
	public Byte apply(Byte val) {
		return m_algorithm.decrypt(val, key);
	}

}
