package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "XorEncryptionAlgorithm")
@ToString()
public class XorEncryptionAlgorithm extends EncryptionAlgorithm {

	@Inject
	public XorEncryptionAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory) {
		super(encryptionApplierFactory,decryptionApplierFactory);
	}
	
	public XorEncryptionAlgorithm() {
	}

	/**
	 * performs xor operation between value and the value of the key, and returns the result
	 */
	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return xor(value,valueOfKey);
	}

	/**
	 * performs xor operation between value and the value of the key, and returns the result
	 */
	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return xor(value,valueOfKey);
	}
	
	private byte xor(byte val1,byte val2) {
		return (byte) (val1 ^ val2);
	}

	/**
	 * returns true if the given key is of type SingleValueKey and false otherwise
	 */
	public boolean isValidKey(Key key) {
		return (key instanceof SingleValueKey);
	}

	@Override
	/**
	 * generates a SingleValueKey with a random value
	 */
	public Key generateKey() {
		return SingleValueKey.generate();
	}

}
