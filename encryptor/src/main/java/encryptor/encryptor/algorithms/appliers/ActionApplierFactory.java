package encryptor.encryptor.algorithms.appliers;

import javax.xml.bind.annotation.XmlType;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;

@XmlType(name = "ActionApplierFactory")
public abstract class ActionApplierFactory {
	public abstract ActionApplier get(EncryptionAlgorithm alg);
}
