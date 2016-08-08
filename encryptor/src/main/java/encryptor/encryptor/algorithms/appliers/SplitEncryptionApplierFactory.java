package encryptor.encryptor.algorithms.appliers;

import javax.xml.bind.annotation.XmlType;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;


@XmlType(name = "SplitEncryptionApplierFactrory")
public class SplitEncryptionApplierFactory extends ActionApplierFactory {
	
	@Override
	public ActionApplier get(EncryptionAlgorithm alg) {
		return new SplitEncryptionApplier(alg);
	}

}
