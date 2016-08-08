package dependencyInjection;

import com.google.inject.AbstractModule;

import encryptor.encryptor.ConsolelUserDialogHandler;
import encryptor.encryptor.EncryptionAlgorithmExecutor;
import encryptor.encryptor.interfaces.UserDialogHandler;
import encryptor.encryptor.xml.XmlParser;

public class DefaultEncryporApplicationModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(UserDialogHandler.class).to(ConsolelUserDialogHandler.class);
		bind(EncryptionAlgorithmExecutor.class).toInstance(new EncryptionAlgorithmExecutor());
		bind(XmlParser.class).toInstance(new XmlParser());
	}

}
