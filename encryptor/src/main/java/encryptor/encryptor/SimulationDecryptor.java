package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SimulationDecryptor {
	
	public void decrypt(File f,OutputStream os) throws IOException {
		if(f == null || os == null) {
			throw new IllegalArgumentException();
		}
		System.out.println("Decrypting:"+f.toPath().toString());
		new EchoStreamer().stream(new FileInputStream(f),os);
	}

}
