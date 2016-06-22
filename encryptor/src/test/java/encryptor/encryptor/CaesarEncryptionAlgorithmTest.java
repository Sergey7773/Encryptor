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
		
		plainValues = new byte[]{Byte.MIN_VALUE+10,0,Byte.MAX_VALUE-10};
		testKey = new byte[]{15};
		cypheredValues = new byte[]{Byte.MIN_VALUE+25,15,Byte.MIN_VALUE+5};
		
		
		for(int i=0;i<plainValues.length;i++) {
			if(plainValues[i]+testKey[0]>Byte.MAX_VALUE){
				cypheredValues[i]=(byte) 
						(Byte.MIN_VALUE+(plainValues[i]+testKey[0]-Byte.MAX_VALUE));
			} else {
				cypheredValues[i] = (byte) (plainValues[i]+testKey[0]);
			}
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
	
	@Test
	public void encryptThenDecryptPlainRetainsValue() {
		for(int i=0;i<plainValues.length;i++) {
			assertEquals($.decrypt($.encrypt(plainValues[i], testKey[0]), testKey[0]),plainValues[i]);
		}
	}
}
