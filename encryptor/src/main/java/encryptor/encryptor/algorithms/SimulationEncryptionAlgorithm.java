package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
public class SimulationEncryptionAlgorithm extends EncryptionAlgorithm{

	@Inject
	public SimulationEncryptionAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory) {
		super(encryptionApplierFactory,decryptionApplierFactory);
	}
	
	public SimulationEncryptionAlgorithm() {
		
	}
	
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
