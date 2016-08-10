package encryptor.encryptor.algorithms.appliers;

import org.junit.Test;
import org.mockito.Mockito;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;

public class SplitDecryptionApplierTest {
	@Test (expected = NullPointerException.class)
	public void throwsNullPointerExceptionOnNullAlgorithm() {
		new SplitDecryptionApplier(null);
	}
	
	@Test
	public void appliesFirstKeyOnEvenIndexesAndSecondKeyOnOddIndexes() {
		CompositeKey key = new CompositeKey(Mockito.mock(Key.class),Mockito.mock(Key.class));
		EncryptionAlgorithm alg = Mockito.mock(EncryptionAlgorithm.class);
		SplitDecryptionApplier $ = new SplitDecryptionApplier(alg);
		for(int i=0;i<200;i++) {
			byte value = (byte)i;
			$.apply(value, key);
			Key keyToVerify = (i%2 == 0) ? key.getFirstKey() : key.getSecondKey();
			Mockito.verify(alg).decrypt(value, keyToVerify);
			
		}
	}
}
