package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EchoStreamer {

	public void stream(InputStream is,OutputStream os) throws IOException {
		if(is == null || os == null)
			throw new IllegalArgumentException("");

		while(is.available()>0) {
			os.write(is.read());
		}
	}
}
