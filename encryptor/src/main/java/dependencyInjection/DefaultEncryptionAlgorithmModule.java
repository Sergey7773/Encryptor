package dependencyInjection;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.algorithms.appliers.DecryptionApplierFactory;
import encryptor.encryptor.algorithms.appliers.EncryptionApplierFactory;

public class DefaultEncryptionAlgorithmModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(ActionApplierFactory.class)
		.annotatedWith(Names.named("encryptionApplierFactory"))
		.to(EncryptionApplierFactory.class);
		
		bind(ActionApplierFactory.class)
		.annotatedWith(Names.named("decryptionApplierFactory"))
		.to(DecryptionApplierFactory.class);
	}
	
}
