package encryptor.encryptor;

import static org.junit.Assert.*;

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
		$ = new EchoStreamer();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionWhenInputStreamIsNull() throws IOException {
		$.stream(null, m_outputStream);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionWhenOutputStreamIsNull() throws IOException {
		$.stream(m_inputStream, null);
	}
	
	@Test
	public void readWholeInputStream() throws IOException {
		m_sourceInputStream.write("random string".getBytes());
		assertTrue(m_inputStream.available()>0);
		
		$.stream(m_inputStream,m_outputStream);
		assertEquals(m_inputStream.available(),0);
	}
	
	@Test
	public void writeReadValuesToOutput() throws IOException {
		PipedInputStream is = new PipedInputStream();
		m_outputStream = new PipedOutputStream(is);
		m_sourceInputStream.write("random string".getBytes());
		$.stream(m_inputStream, m_outputStream);
		int bytes=is.available();
		byte[] buffer = new byte[bytes];
		is.read(buffer);
		for(int i=0; i<bytes;i++)
			assertEquals("random string".getBytes()[i],buffer[i]);
	}
}
