package encryptor.encryptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class EchoStreamerTest {
	
	private EchoStreamer $;
	private OutputStream m_outputStream;
	private InputStream m_inputStream;
	private PipedOutputStream m_sourceInputStream;
	
	@Before
	public void setup() throws IOException {
		m_outputStream = new PrintStream(new PipedOutputStream());
		m_sourceInputStream = new PipedOutputStream();
		m_inputStream = new PipedInputStream(m_sourceInputStream);
		
		$ = new EchoStreamer(m_outputStream);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionOnNullArgumentToConstroctor() {
		new EchoStreamer(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwIlleglArgumentExceptionOnNullInputStream() throws IOException {
		$.stream(null);
	}
}
