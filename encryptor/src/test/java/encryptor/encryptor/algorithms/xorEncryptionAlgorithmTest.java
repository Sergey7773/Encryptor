package encryptor.encryptor.algorithms;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.XorEncryptionAlgorithm;

public class xorEncryptionAlgorithmTest {
	
	private XorEncryptionAlgorithm $;
	private byte[] plainText;
	private byte[] cypheredText;
	private byte[] key;
	
	@Before
	public void setup() {
		$ = new XorEncryptionAlgorithm();
		key = new byte[1];
		new Random(System.currentTimeMillis()).nextBytes(key);
		plainText = new byte[]{Byte.MIN_VALUE,-100,-66,0,66,100,Byte.MAX_VALUE};
		cypheredText = new byte[plainText.length];
		for(int i=0;i<cypheredText.length;i++) {
			cypheredText[i] = (byte) (plainText[i]^key[0]);
		}
	}
	
	@Test
	public void AppliesXorOnEncryptedValues() {
		for(int i=0;i<plainText.length;i++)
			assertEquals(cypheredText[i],$.encrypt(plainText[i], new SingleValueKey(key[0])));
	}
	
	@Test
	public void AppliesXorOnDecryptedValues() {
		for(int i=0;i<cypheredText.length;i++)
			assertEquals(plainText[i],$.encrypt(cypheredText[i], new SingleValueKey(key[0])));
	}
	
}
