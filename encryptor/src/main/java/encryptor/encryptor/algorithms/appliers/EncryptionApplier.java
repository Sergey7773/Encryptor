package encryptor.encryptor.algorithms.appliers;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;

public class EncryptionApplier implements ActionApplier {

	private EncryptionAlgorithm m_algorithm;
	public EncryptionApplier(EncryptionAlgorithm alg) {
		this.m_algorithm=alg;
	}
	
	public Byte apply(Byte val,Key key) {
		return m_algorithm.encrypt(val, key);
	}

}
