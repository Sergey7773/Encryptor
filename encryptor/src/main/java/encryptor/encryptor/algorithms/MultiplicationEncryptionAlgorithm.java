package encryptor.encryptor.algorithms;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "MultiplicationEncryptionAlgorithm")
public class MultiplicationEncryptionAlgorithm extends EncryptionAlgorithm{
	
	private byte lastKey;
	private byte decKey;
	
	public MultiplicationEncryptionAlgorithm() {
		lastKey=1;
		decKey=1;
	}
	
	public byte encrypt(byte value, Key key) {
		if(!isValidKey(key))
			throw new IllegalArgumentException();
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return MWO(value,valueOfKey);
	}

	public byte decrypt(byte value, Key key) {
		if(!isValidKey(key))
			throw new IllegalArgumentException();
		byte valueOfKey = ((SingleValueKey)key).getValue();
		if(lastKey!=valueOfKey) {
			lastKey=valueOfKey;
			decKey = findNewDecKey();
		}
		return MWO(value,decKey);
	}
	
	private byte MWO(byte val1,byte val2) {
		return (byte)(val1*val2);
	}
	
	private byte findNewDecKey() {
		for(byte k = Byte.MIN_VALUE; k<=Byte.MAX_VALUE; k++) {
			if(MWO(k,lastKey)==1)
				return k;
		}
		return -1;
	}

	public boolean isValidKey(Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		if(valueOfKey == 0 || valueOfKey == 2) return false;
		return true;
	}

	@Override
	public Key generateKey() {
		return SingleValueKey.generate(Arrays.asList((byte)0, (byte)2));
	}

}
