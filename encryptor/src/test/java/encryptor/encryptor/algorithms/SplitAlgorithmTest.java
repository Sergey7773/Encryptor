package encryptor.encryptor.algorithms;


import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.interfaces.Key;

public class SplitAlgorithmTest {
	private SplitAlgorithm $;
	private EncryptionAlgorithm nestedAlgorithm;
	private CompositeKey testKey;
	private SingleValueKey firstSubKey, secondSubKey;
	private byte testValue = (byte)15;
	
	@Before
	public void setup() {
		nestedAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
		Mockito.doReturn(true).when(nestedAlgorithm).isValidKey(Mockito.any());
		firstSubKey = Mockito.mock(SingleValueKey.class);
		secondSubKey = Mockito.mock(SingleValueKey.class);
		testKey = new CompositeKey(firstSubKey,secondSubKey);
		$ = new SplitAlgorithm(nestedAlgorithm);
	}
	
	@Test
	public void applyNestedAlgorithmWhenEncrypting() {
		$.encrypt(testValue,testKey);
		Mockito.verify(nestedAlgorithm, Mockito.times(1)).encrypt(Mockito.eq(testValue), Mockito.any());
	}
	
	@Test
	public void applyNestedAlgorithmWhenDecrypting() {
		$.decrypt(testValue, testKey);
		Mockito.verify(nestedAlgorithm, Mockito.times(1)).decrypt(Mockito.eq(testValue), Mockito.any());
	}
	
	@Test
	public void applyFirstSubKeyOnEvenBytesAndSecondSubKeyOnOddWhenEncrypting() throws IOException {
		byte[] plainTextBuffer = new byte[100];
		new Random(System.currentTimeMillis()).nextBytes(plainTextBuffer);
		PipedOutputStream source = new PipedOutputStream();
		PipedInputStream sink = new PipedInputStream(source);
		source.write(plainTextBuffer);
		OutputStream output = new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
			}
		};
		
		$.encrypt(sink, output, testKey);
		
		for(int i=0;i<plainTextBuffer.length;i++) {
			SingleValueKey keyToVerify = (i%2 == 0) ? firstSubKey : secondSubKey;
			Mockito.verify(nestedAlgorithm,Mockito.atLeastOnce()).encrypt(plainTextBuffer[i], keyToVerify);
		}
	}
	
	@Test
	public void applyFirstSubKeyOnEvenBytesAndSecondSubKeyOnOddWhenDecrypting() throws IOException {
		byte[] cypheredTextBuffer = new byte[100];
		new Random(System.currentTimeMillis()).nextBytes(cypheredTextBuffer);
		PipedOutputStream source = new PipedOutputStream();
		PipedInputStream sink = new PipedInputStream(source);
		source.write(cypheredTextBuffer);
		OutputStream output = new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
			}
		};
		
		$.decrypt(sink, output, testKey);
		
		for(int i=0;i<cypheredTextBuffer.length;i++) {
			SingleValueKey keyToVerify = (i%2 == 0) ? firstSubKey : secondSubKey;
			Mockito.verify(nestedAlgorithm,Mockito.atLeastOnce()).decrypt(cypheredTextBuffer[i], keyToVerify);
		}
	}
	
	@Test
	public void returnValueReturnedByNestedAlgorithmOnEncrypt() {
		Mockito.doReturn((byte)1).when(nestedAlgorithm).encrypt(Mockito.anyByte(), Mockito.any());
		assertEquals((byte)1,$.encrypt(testValue, testKey));
	}
	
	@Test
	public void returnValueReturnedByNestedAlgorithmOnDecrypt() {
		Mockito.doReturn((byte)2).when(nestedAlgorithm).decrypt(Mockito.anyByte(), Mockito.any());
		assertEquals((byte)2, $.decrypt(testValue, testKey));
	}
	
	@Test
	public void propagatesKeyValidityFromNestedAlgorithm() {
		assertTrue($.isValidKey(testKey));
		Mockito.verify(nestedAlgorithm).isValidKey(testKey);
		Mockito.doReturn(false).when(nestedAlgorithm).isValidKey(Mockito.any());
		assertFalse($.isValidKey(testKey));
		Mockito.verify(nestedAlgorithm, Mockito.times(2)).isValidKey(testKey);
	}
	
	
}
