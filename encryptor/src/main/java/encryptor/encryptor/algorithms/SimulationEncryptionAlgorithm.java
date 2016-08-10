package encryptor.encryptor.algorithms;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.appliers.ActionApplier;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
public class SimulationEncryptionAlgorithm extends EncryptionAlgorithm{

	@Inject
	public SimulationEncryptionAlgorithm(
			@Named("encryptionApplierFactory")String encApplierClassName,
			@Named("decryptionApplierFactory")String decApplierClassName,
			ClassLoader classLoader) {
		super(encApplierClassName,decApplierClassName,classLoader);
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

	@Override
	public ActionApplier getEncryptionApplier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionApplier getDecryptionApplier() {
		// TODO Auto-generated method stub
		return null;
	}

}
