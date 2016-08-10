package dependencyInjection;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import encryptor.encryptor.algorithms.appliers.ActionApplier;
import encryptor.encryptor.algorithms.appliers.ApplierFactory;
import encryptor.encryptor.algorithms.appliers.DecryptionApplier;
import encryptor.encryptor.algorithms.appliers.EncryptionApplier;

public class DefaultEncryptionAlgorithmModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(String.class)
		.annotatedWith(Names.named("encryptionApplierFactory"))
		.toInstance(EncryptionApplier.class.getName());
		
		bind(String.class)
		.annotatedWith(Names.named("decryptionApplierFactory"))
		.toInstance(DecryptionApplier.class.getName());
	}
	
}
