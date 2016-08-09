package dependencyInjection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import encryptor.encryptor.ConsolelUserDialogHandler;
import encryptor.encryptor.EncryptionAlgorithmExecutor;
import encryptor.encryptor.interfaces.UserDialogHandler;
import encryptor.encryptor.xml.XmlParser;

public class DefaultEncryporApplicationModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(UserDialogHandler.class).to(ConsolelUserDialogHandler.class);
		bind(EncryptionAlgorithmExecutor.class).toInstance(
				Guice.createInjector(new DefaultEncryptionAlgorithmExecutorModule()).
				getInstance(EncryptionAlgorithmExecutor.class));
		bind(XmlParser.class).toInstance(new XmlParser());
	}

}
