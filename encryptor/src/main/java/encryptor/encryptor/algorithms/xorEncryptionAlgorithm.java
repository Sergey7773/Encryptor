package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "XorEncryptionAlgorithm")
public class XorEncryptionAlgorithm extends EncryptionAlgorithm {

	@Inject
	public XorEncryptionAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory) {
		super(encryptionApplierFactory,decryptionApplierFactory);
	}
	
	public XorEncryptionAlgorithm() {
	}

	public byte encrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return xor(value,valueOfKey);
	}

	public byte decrypt(byte value, Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return xor(value,valueOfKey);
	}
	
	private byte xor(byte val1,byte val2) {
		return (byte) (val1 ^ val2);
	}

	public boolean isValidKey(Key key) {
		return true;
	}

	@Override
	public Key generateKey() {
		return SingleValueKey.generate();
	}

}
