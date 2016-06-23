package encryptor.encryptor;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
	
	@Test
	public void appliesEncryptionFunctionToEveryByteInInputFIle() throws IOException {
		File inputFile = folder.newFile("file1.txt");
		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] plainText = new byte[]{10,20,30,40,50};
		
		os.write(plainText);
		
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		
		$.encrypt(inputFile, userOutput);
		
		byte key = (byte) is.read();
		for(int i=0;i<plainText.length;i++) {
			Mockito.verify(mockAlgorithm).encrypt(plainText[i], key);
		}
		inputFile.delete();
		os.close();
		
	}
	
	@Test
	public void createsDecryptedFileWithCorrectAppendix() throws IOException {
		File inputFile = folder.newFile("file1.txt");
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		
		$.encrypt(inputFile, userOutput);
		byte key = (byte)(is.read());
		$.decrypt(new File(inputFile.getPath()+".encrypted"), userOutput, key);
		File decrypted = new File(inputFile.getParentFile().getPath()+"\\file1_decrypted.txt");
		assertTrue(decrypted.exists());
	}
	
	@Test
	public void appliesDecryptionFunctionToEveryByteInInputFile() throws IOException {
		File inputFile = folder.newFile("file1.txt.encrypted");
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		
		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] cypheredText = new byte[]{10,20,30,40,50};
		
		os.write(cypheredText);
		os.close();
		
		$.decrypt(inputFile, userOutput, (byte)0);
		for(int i=0;i<cypheredText.length;i++) {
			Mockito.verify(mockAlgorithm).decrypt(cypheredText[i], (byte)0);
		}
	}
	
	@Test
	public void writesEveryEncryptedByteToNewFile() throws IOException {
		File inputFile = folder.newFile("file1.txt");
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		
		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] plainText = new byte[]{10,20,30,40,50};
		
		os.write(plainText);
		os.close();
		
		$.encrypt(inputFile, userOutput);
		
		File encyptedFile = new File(inputFile.getPath()+".encrypted");
		FileInputStream fis = new FileInputStream(encyptedFile);
		int available = fis.available();
		fis.close();
		assertEquals(plainText.length, available);
	}
	
	@Test
	public void writeEveryDecryptedByteToNewDecryptedFile() throws IOException {
		File inputFile = folder.newFile("file1.txt.encrypted");
		
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		
		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] cypheredText = new byte[]{10,20,30,40,50};
		
		os.write(cypheredText);
		os.close();
		
		$.decrypt(inputFile, userOutput, (byte)0);
		
		File decryptedFile = new File(inputFile.getParentFile().getPath()+"\\file1_decrypted.txt");
		FileInputStream fis = new FileInputStream(decryptedFile);
		int available = fis.available();
		fis.close();
		assertEquals(cypheredText.length,available);
	}
	
}
