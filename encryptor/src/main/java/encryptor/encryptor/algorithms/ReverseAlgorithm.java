package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.Key;

@XmlRootElement
@XmlType(name = "ReverseAlgorithm")
public class ReverseAlgorithm extends EncryptionAlgorithm {
	
	@XmlElement
	private EncryptionAlgorithm nestedAlgorithm;
	
	public ReverseAlgorithm() {
		this.nestedAlgorithm = null;
	}
	
	public ReverseAlgorithm(EncryptionAlgorithm nested) {
		this.nestedAlgorithm = nested;
	}
	
	@Override
	public byte encrypt(byte value, Key key) {
		return nestedAlgorithm.decrypt(value, key);
	}

	@Override
	public byte decrypt(byte value, Key key) {
		return nestedAlgorithm.encrypt(value, key);
	}

	@Override
	public boolean isValidKey(Key key) {
		return nestedAlgorithm.isValidKey(key);
	}

	@Override
	public Key generateKey() {
		return nestedAlgorithm.generateKey();
	}
}
