package dependencyInjection;

import com.google.inject.AbstractModule;

import encryptor.encryptor.ConsolelUserDialogHandler;
import encryptor.encryptor.interfaces.UserDialogHandler;

public class DefaultEncryporApplicationModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(UserDialogHandler.class).to(ConsolelUserDialogHandler.class);
	}

}
