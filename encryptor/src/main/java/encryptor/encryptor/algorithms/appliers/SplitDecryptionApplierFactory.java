package encryptor.encryptor.algorithms.appliers;

import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;

@XmlType(name = "SplitDecryptionApplierFactrory")
public class SplitDecryptionApplierFactory extends ActionApplierFactory {

	@Override
	public ActionApplier get(EncryptionAlgorithm alg) {
		return new SplitDecryptionApplier(alg);
	}

}
