package encryptor.encryptor.simulation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import encryptor.encryptor.simulation.SimulationDecryptor;

public class SimulationDecryptorTest {
	private SimulationDecryptor $;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before 
	public void setup() {
		$ = new SimulationDecryptor();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionOnNullFile() throws IOException {
		$.decrypt(null, new PipedOutputStream());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionOnNullOutputSream() throws IOException {
		$.decrypt(new File(""), null);
	}
	
	@Test
	public void echoFileContent() throws IOException {
		PipedOutputStream os = new PipedOutputStream();
		PipedInputStream is = new PipedInputStream(os);
		File tmpFile = folder.newFile("test_file");
		FileOutputStream fos = new FileOutputStream(tmpFile);
		fos.write("content".getBytes());
		fos.close();
		$.decrypt(tmpFile, os);
		int availableBytes = is.available();
		byte[] bytes = new byte[availableBytes];
		is.read(bytes);
		for(int i=0;i<availableBytes;i++)
			assertEquals(bytes[i],"content".getBytes()[i]);
		is.close();
	}
}
