package dependencyInjection;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import encryptor.encryptor.ActionObserver;
import encryptor.encryptor.interfaces.Observer;

public class DefaultEncryptionAlgorithmExecutorModule extends AbstractModule {

	@Override
	protected void configure() {
		
		List<Observer> encryptionObservers = new ArrayList<Observer>();
		List<Observer> decryptionObservers = new ArrayList<Observer>();
		encryptionObservers.add(new ActionObserver("Encryption started.",
				"Encryption ended."));
		decryptionObservers.add(new ActionObserver("Decryption started",
				"Decryption ended"));
		
		bind(List.class).annotatedWith(Names.named("encObservers")).toInstance(encryptionObservers);
		bind(List.class).annotatedWith(Names.named("decObservers")).toInstance(decryptionObservers);
		
	}

}
