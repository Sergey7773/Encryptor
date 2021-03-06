package dependencyInjection;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import encryptor.encryptor.ActionObserver;
import encryptor.encryptor.interfaces.Observer;
import encryptor.encryptor.xml.XmlParser;

public class DefaultEncryptionAlgorithmExecutorModule extends AbstractModule {

	@Override
	protected void configure() {
		
		List<Observer> encryptionObservers = new ArrayList<Observer>();
		List<Observer> decryptionObservers = new ArrayList<Observer>();
		encryptionObservers.add(new ActionObserver("Encryption started.",
				"Encryption ended."));
		decryptionObservers.add(new ActionObserver("Decryption started",
				"Decryption ended"));
		
		bind(new TypeLiteral<List<Observer>>() {}).annotatedWith(Names.named("encObservers")).toInstance(encryptionObservers);
		bind(new TypeLiteral<List<Observer>>() {}).annotatedWith(Names.named("decObservers")).toInstance(decryptionObservers);
		bind(XmlParser.class).toInstance(new XmlParser());
	}

}
