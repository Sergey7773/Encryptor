package encryptor.encryptor.algorithms;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import encryptor.encryptor.SingleValueKey;

public class CaesarEncryptionAlgorithmTest {
	
	private CaesarEncryptionAlgorithm $;
	private byte[] plainValues;
	private byte[] testKey;
	private byte[] cypheredValues;
	
	
	@Before
	public void setup() {
		$ = new CaesarEncryptionAlgorithm();
		
		plainValues = new byte[]{Byte.MIN_VALUE+10,0,Byte.MAX_VALUE-10};
		testKey = new byte[]{15};
		cypheredValues = new byte[]{Byte.MIN_VALUE+25,15,(byte) (Byte.MAX_VALUE+5)};
	}
	
	@Test
	public void addKeyToValueWithOverflowOnEncryption() {

		for(int i=0;i<plainValues.length;i++) {
			assertEquals($.encrypt(plainValues[i],
					new SingleValueKey(testKey[0])),cypheredValues[i]);
		}
	}
	
	@Test
	public void removeKeyFromValueWithOverflowOnDecrypion() {
		for(int i=0;i<plainValues.length;i++) {
			assertEquals($.decrypt(cypheredValues[i], 
					new SingleValueKey(testKey[0])),plainValues[i]);
		}
	}
	
	@Test
	public void encryptThenDecryptPlainRetainsValue() {
		for(int i=0;i<plainValues.length;i++) {
			assertEquals($.decrypt($.encrypt(plainValues[i], 
					new SingleValueKey(testKey[0])),
					new SingleValueKey(testKey[0])),plainValues[i]);
		}
	}
}
