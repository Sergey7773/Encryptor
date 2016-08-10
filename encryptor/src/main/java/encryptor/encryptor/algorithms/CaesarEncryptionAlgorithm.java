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
@XmlType(name = "CaesarEncryptionAlgorithm")
@ToString()
public class CaesarEncryptionAlgorithm extends EncryptionAlgorithm {

	@Inject
	public CaesarEncryptionAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory) {
		super(encryptionApplierFactory,decryptionApplierFactory);
	}
	
	public CaesarEncryptionAlgorithm() {
	}
	
	
	/**
	 * adds the value of the key to the byte (with overflow) and returns the result.
	 */
	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return (byte)(value+valueOfKey);
	}
	
	/**
	 * substracts the value of the key to the byte (with overflow) and returns the result.
	 */
	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return (byte)(value-valueOfKey);
	}

	/**
	 * returns true if the given key is of type SingleValueKey
	 */
	public boolean isValidKey(Key key) {
		return (key instanceof SingleValueKey);
	}

	@Override
	/**
	 * returns a new SingleValueKey
	 */
	public Key generateKey() {
		return SingleValueKey.generate();
	}

}
