package encryptor.encryptor;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import encryptor.encryptor.algorithms.MultiplicationEncryptionAlgorithm;

public class MultiplicationEncryptionAlgorithmTest {
	private MultiplicationEncryptionAlgorithm $;
	private byte[] plainText;
	private byte[] key;
	private byte[] cypheredText;
	
	@Before
	public void setup() {
		$ = new MultiplicationEncryptionAlgorithm();
		plainText = new byte[]{Byte.MIN_VALUE, -75,0,75,Byte.MAX_VALUE};
		key = new byte[1];
		cypheredText = new byte[plainText.length];
		do {
			new Random(System.currentTimeMillis()).nextBytes(key);
		} while(key[0]==0 || key[0]==2);
		for(int i=0;i<plainText.length;i++) {
			cypheredText[i] = (byte) (key[0]*plainText[i]);
		}
	}
	
	@Test
	public void applyingMWOOnEncryptedBytes() {
		for(int i=0;i<plainText.length;i++)
			assertEquals(cypheredText[i],$.encrypt(plainText[i], new SingleValueKey(key[0])));
	}
	
	@Test
	public void correctlyDecrypting() {
		for(int i=0;i<plainText.length;i++) {
			assertEquals(plainText[i],$.decrypt(cypheredText[i], new SingleValueKey(key[0])));
		}
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwsExceptionWhenKeyEqualsZeroAndDecrypting() {
		$.decrypt((byte)100, new SingleValueKey((byte)0));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwsExceptionWhenKeyEqualsTwoAndDecrypting() {
		$.decrypt((byte)100, new SingleValueKey((byte)2));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwsExceptionWhenKeyEqualsZeroAndEncrypting() {
		$.encrypt((byte)100, new SingleValueKey((byte)0));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwsExceptionWhenKeyEqualsTwoAndEncrypting() {
		$.encrypt((byte)100, new SingleValueKey((byte)2));
	}
	
}
