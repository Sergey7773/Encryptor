package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "CaesarEncryptionAlgorithm")
public class CaesarEncryptionAlgorithm extends EncryptionAlgorithm {

	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		/*int delta = Byte.MAX_VALUE-value-valueOfKey;
		if(delta<0) {
			return (byte)(Byte.MIN_VALUE-delta);
		} */
		return (byte)(value+valueOfKey);
	}

	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		/*int delta = value-valueOfKey-Byte.MIN_VALUE;
		if(delta<0) {
			return (byte)(Byte.MAX_VALUE+delta);
		}*/
		return (byte)(value-valueOfKey);
	}

	public boolean isValidKey(Key key) {
		return true;
	}

	@Override
	public Key generateKey() {
		return SingleValueKey.generate();
	}

}
