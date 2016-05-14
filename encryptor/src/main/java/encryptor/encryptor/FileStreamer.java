package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileStreamer {
	
	public void stream(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		OutputStream os = System.out;
		int c;
		while((c=fis.read())!=-1) {
			os.write(c);
		}
		fis.close();
	}
}
