package encryptor.encryptor;


import com.google.inject.Guice;
import com.google.inject.Injector;

import dependencyInjection.DefaultEncryporApplicationModule;


public class Main {

	public static void main(String[] args){
		
		Injector injector = Guice.createInjector(new DefaultEncryporApplicationModule());
		EncryptorApplication app = injector.getInstance(EncryptorApplication.class);
		
		try {
			app.run();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
