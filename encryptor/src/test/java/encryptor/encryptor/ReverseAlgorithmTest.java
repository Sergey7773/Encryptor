package encryptor.encryptor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.algorithms.ReverseAlgorithm;
import encryptor.encryptor.interfaces.Key;

public class ReverseAlgorithmTest {
	
	private ReverseAlgorithm $;
	private EncryptionAlgorithm nestedMock;
	private byte testValue;
	private Key testKey;
	
	@Before
	public void setup() {
		nestedMock = Mockito.mock(EncryptionAlgorithm.class);
		$ = new ReverseAlgorithm(nestedMock);
		testKey = new SingleValueKey((byte)16);
		testValue = (byte)16;
		
		Mockito.doReturn((byte)1).when(nestedMock).encrypt(testValue, testKey);
		Mockito.doReturn((byte)2).when(nestedMock).decrypt(testValue, testKey);
	}
	
	@Test
	public void returnResultOfDecryptionByNestedAlgorithmWhenEncrypting() {
		assertEquals(2,$.encrypt(testValue, testKey));
		Mockito.verify(nestedMock).decrypt(testValue, testKey);
	}
	
	@Test
	public void returnResultOFEncryptionByNestedAlgorithmWhenDecrypting() {
		assertEquals(1,$.decrypt(testValue, testKey));
		Mockito.verify(nestedMock).encrypt(testValue, testKey);
	}
}
