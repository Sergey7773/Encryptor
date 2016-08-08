package encryptor.encryptor.algorithms.appliers;

import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;

@XmlType(name = "DecryptionApplierFactory")
public class DecryptionApplierFactory extends ActionApplierFactory {
	@Override
	public ActionApplier get(EncryptionAlgorithm alg) {
		return new DecryptionApplier(alg);
	}

}
