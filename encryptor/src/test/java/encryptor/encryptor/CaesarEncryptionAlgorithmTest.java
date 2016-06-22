package encryptor.encryptor;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class CaesarEncryptionAlgorithmTest {
	
	private CaesarEncryptionAlgorithm $;
	private byte[] plainValues;
	private byte[] testKey;
	private byte[] cypheredValues;
	
	
	@Before
	public void setup() {
		$ = new CaesarEncryptionAlgorithm();
		
		Random randGen = new Random(System.currentTimeMillis());
		plainValues = new byte[20];
		randGen.nextBytes(plainValues);
		testKey = new byte[1];
		randGen.nextBytes(testKey);
		
		cypheredValues = new byte[20];
		for(int i=0;i<plainValues.length;i++) {
			cypheredValues[i] = (byte) (plainValues[i]+testKey[0]);
		}
	}
	
	@Test
	public void addKeyToValueWithOverflowOnEncryption() {

		for(int i=0;i<plainValues.length;i++) {
			assertEquals($.encrypt(plainValues[i], testKey[0]),cypheredValues[i]);
		}
	}
	
	@Test
	public void removeKeyFromValueWithOverflowOnDecrypion() {
		for(int i=0;i<plainValues.length;i++) {
			assertEquals($.decrypt(cypheredValues[i], testKey[0]),plainValues[i]);
		}
	}
}
