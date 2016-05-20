package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EchoStreamer {
	private OutputStream m_outputStream;
	
	public EchoStreamer() {
		this(System.out);
	}
	
	public EchoStreamer(OutputStream os) {
		if(os == null) 
			throw new IllegalArgumentException("OutputStream is null");
		m_outputStream=os;
	}
	
	public void stream(InputStream is) throws IOException {
		if(is == null)
			throw new IllegalArgumentException("InputStream is null");
		int c;
		while((c=is.read())!=-1) {
			m_outputStream.write(c);
		}
	}
}
