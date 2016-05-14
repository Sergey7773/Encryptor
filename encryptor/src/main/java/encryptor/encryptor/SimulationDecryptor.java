package encryptor.encryptor;

import java.io.File;
import java.io.IOException;

public class SimulationDecryptor {
	
	public void Decrypt(File f) throws IOException {
		System.out.println("Decrypting:"+f.toPath().toString());
		new FileStreamer().stream(f);
	}

}
