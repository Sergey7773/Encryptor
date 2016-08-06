package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlRootElement;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
public class SimulationEncryptionAlgorithm extends EncryptionAlgorithm{

	@Override
	public byte encrypt(byte value, Key key) {
		return value;
	}

	@Override
	public byte decrypt(byte value, Key key) {
		return value;
	}

	@Override
	public boolean isValidKey(Key key) {
		return true;
	}

	@Override
	public Key generateKey() {
		return SingleValueKey.generate();
	}

}
