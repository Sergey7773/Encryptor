package encryptor.encryptor.algorithms;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.SingleValueKey;

public class DoubleAlgorithmTest {

	private DoubleAlgorithm $;
	private CompositeKey testKey;
	private byte testValue;
	private EncryptionAlgorithm firstMock, secondMock;
	
	@Before
	public void setup() {
		firstMock = Mockito.mock(EncryptionAlgorithm.class);
		secondMock = Mockito.mock(EncryptionAlgorithm.class);
		$ = new DoubleAlgorithm(firstMock, secondMock);
		testKey = new CompositeKey(new SingleValueKey((byte)1), new SingleValueKey((byte)2));
		testValue = (byte)16;
		Mockito.doReturn((byte)3).when(firstMock).decrypt(Mockito.anyByte(), Mockito.any());
		Mockito.doReturn((byte)4).when(secondMock).decrypt(Mockito.anyByte(), Mockito.any());
		Mockito.doReturn((byte)1).when(firstMock).encrypt(Mockito.anyByte(), Mockito.any());
		Mockito.doReturn((byte)2).when(secondMock).encrypt(Mockito.anyByte(), Mockito.any());
		
	}
	
	@Test
	public void onEncryptUseAlgorithmsInOrder() {
		InOrder inOrder = Mockito.inOrder(firstMock, secondMock);
		$.encrypt(testValue, testKey);
		
		inOrder.verify(firstMock).encrypt(Mockito.anyByte(), Mockito.any());
		inOrder.verify(secondMock).encrypt(Mockito.anyByte(), Mockito.any());
	}
	
	@Test
	public void onDecryptUseAlgorithmsInReverseOrder() {
		InOrder inOrder = Mockito.inOrder(secondMock, firstMock);
		$.decrypt(testValue, testKey);
		
		inOrder.verify(secondMock).decrypt(Mockito.anyByte(), Mockito.any());
		inOrder.verify(firstMock).decrypt(Mockito.anyByte(), Mockito.any());
	}
	
	@Test
	public void notUsingDecryptWhileEncrypting() {
		$.encrypt(testValue, testKey);
		
		Mockito.verify(firstMock, Mockito.never()).decrypt(Mockito.anyByte(), Mockito.any());
		Mockito.verify(secondMock, Mockito.never()).decrypt(Mockito.anyByte(), Mockito.any());
	}
	
	@Test
	public void notUsingEncryptionWhileDecrypting() {
		$.decrypt(testValue, testKey);
		
		Mockito.verify(firstMock, Mockito.never()).encrypt(testValue, testKey);
		Mockito.verify(secondMock, Mockito.never()).decrypt(testValue, testKey);
	}
	
	@Test
	public void WhenEncryptingApplyingFirstAlgorithmWithPassedValueAndFirstSubKeyOnce() {
		$.encrypt(testValue, testKey);
		
		Mockito.verify(firstMock, Mockito.times(1)).encrypt(testValue, testKey.getFirstKey());
	}
	
	@Test
	public void WhenEncryptingApplyingSecondAlgOnResultOfFirstWithSecondSubKeyOnce() {
		$.encrypt(testValue, testKey);
		
		Mockito.verify(secondMock, Mockito.times(1)).encrypt((byte)1, testKey.getSecondKey());
	}
	
	@Test
	public void WhenDecryptingApplyingSecondAlgOnPassedValueAndSecondSubKeyOnce() {
		$.decrypt(testValue, testKey);
		
		Mockito.verify(secondMock, Mockito.times(1)).decrypt(testValue, testKey.getSecondKey());
	}
	
	@Test
	public void WhenDecryptingApplyingFirstAlgOnResultOfSecondWithFirstSubKeyOnce() {
		$.decrypt(testValue, testKey);
		
		Mockito.verify(firstMock, Mockito.times(1)).decrypt((byte)4, testKey.getFirstKey());
	}
	
	@Test
	public void returnCorrectValuesOnEcryptionAndDecryption() {
		assertEquals(2,$.encrypt(testValue, testKey));
		assertEquals(3,$.decrypt(testValue, testKey));
	}
	
	
}
