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
	
	
	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return (byte)(value+valueOfKey);
	}

	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
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
