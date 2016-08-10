package encryptor.encryptor.algorithms.appliers;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;

public class ApplierFactory{
	private Class<? extends ActionApplier> type;
	
	public ApplierFactory(Class<? extends ActionApplier> type) {
		this.type = type;
	}
	
	public Class<? extends ActionApplier> getType() {
		return this.type;
	}
	
	public ActionApplier get(EncryptionAlgorithm alg) {
		try {
			return type.getConstructor(EncryptionAlgorithm.class).newInstance(alg);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
