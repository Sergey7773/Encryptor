package encryptor.encryptor;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;

public class EncryptionApplier implements Applier<Byte,Byte> {

	private EncryptionAlgorithm m_algorithm;
	private Key key;
	public EncryptionApplier(EncryptionAlgorithm alg,Key key) {
		this.key=key;
		this.m_algorithm=alg;
	}
	
	public Byte apply(Byte val) {
		return m_algorithm.encrypt(val, key);
	}

}
