package encryptor.encryptor;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class AlgorithmWrapperTest {
	private AlgorithmWrapper $;
	private EncryptionAlgorithm mockAlgorithm;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before
	public void setup() {
		mockAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
		$ = new AlgorithmWrapper(mockAlgorithm);
	}
	
	@Test
	public void printKeyToUserOutputStreamOnEncrypt() throws IOException {
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		File inputFile = folder.newFile("file1.txt");
		$.encrypt(inputFile, userOutput);
		assertTrue(is.available()>0);
		is.close();
		userOutput.close();
	}
	
	@Test
	public void createsEncryptedFileWithCorrectAppendix() throws IOException {
		ByteArrayOutputStream userOutput = new ByteArrayOutputStream(1);
		File inputFile = folder.newFile("file1.txt");
		File resultFile = new File(inputFile+".encrypted");
		if(resultFile.exists()){
			resultFile.delete();
		}
		$.encrypt(inputFile, userOutput);
		assertTrue(resultFile.exists());
		resultFile.delete();
		userOutput.close();
	}
	
}
