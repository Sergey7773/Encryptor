package encryptor.encryptor;

import java.io.File;
import java.io.IOException;

public class SimulationEncryptor {
	
	public void encrypt(File f) throws IOException {
		System.out.println("Encrypting:"+f.getPath().toString());
		new FileStreamer().stream(f);
	}
}
