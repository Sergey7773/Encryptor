package encryptor.encryptor.algorithms.appliers;

import org.junit.Test;
import org.mockito.Mockito;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;

public class DecryptionApplierTest {

	private EncryptionAlgorithm mockAlgorithm;
	private DecryptionApplier $;
	private Key mockKey;

	@Test (expected = NullPointerException.class)
	public void throwsNullPointerExceptionOnNullAlgorithm() {
		$ = new DecryptionApplier(null);
	}
	
	@Test
	public void callsDecryptOfGivenAlgorithmWithPassedKeyAndValue() {
		mockKey = Mockito.mock(Key.class);
		for(byte i = Byte.MIN_VALUE; i<Byte.MAX_VALUE;i++) {
			mockAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
			$ = new DecryptionApplier(mockAlgorithm);
			$.apply(i, mockKey);
			Mockito.verify(mockAlgorithm, Mockito.times(1)).decrypt(i, mockKey);
		}

	}

}
