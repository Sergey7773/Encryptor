package encryptor.encryptor.algorithms.appliers;

import lombok.NonNull;
import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;

public class SplitEncryptionApplier implements ActionApplier {
	private int counter = 0;
	private EncryptionAlgorithm alg;
	
	public SplitEncryptionApplier(@NonNull EncryptionAlgorithm alg) {
		this.alg = alg;
	}
	
	@Override
	public Byte apply(Byte t, Key u) {
		Key usedKey = (counter%2==0) ? ((CompositeKey)u).getFirstKey() : ((CompositeKey)u).getSecondKey();
		counter++;
		return alg.encrypt(t, usedKey);
	}
	
	
}
