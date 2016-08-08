package dependencyInjection;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class DefaultEncryptionAlgorithmExecutorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(List.class).annotatedWith(Names.named("encObservers")).to(ArrayList.class);
		bind(List.class).annotatedWith(Names.named("decObservers")).to(ArrayList.class);
		
	}

}
