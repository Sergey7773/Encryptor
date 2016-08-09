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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.Observer;

public class EncrypionAlgorithmExecutorTest {
	private EncryptionAlgorithm mockAlgorithm;
	private EncryptionAlgorithmExecutor $;
	private List<Observer> encryptionObservers;
	private List<Observer> decryptionObservers;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private Observer getMockObserver() {
		return Mockito.mock(Observer.class);
	}

	@Before
	public void setup() throws IOException {
		mockAlgorithm = Mockito.mock(EncryptionAlgorithm.class);
		encryptionObservers = new ArrayList<Observer>();
		encryptionObservers.addAll(Arrays.asList(getMockObserver(),getMockObserver()));
		decryptionObservers = new ArrayList<Observer>();
		decryptionObservers.addAll(Arrays.asList(getMockObserver(),getMockObserver()));
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(new TypeLiteral<List<Observer>>() {}).annotatedWith(Names.named("encObservers")).toInstance(encryptionObservers);
				bind(new TypeLiteral<List<Observer>>() {}).annotatedWith(Names.named("decObservers")).toInstance(decryptionObservers);
			}
		});

		$ = injector.getInstance(EncryptionAlgorithmExecutor.class);
		
		folder.create();

	}
	
	@After
	public void cleanup() {
		folder.delete();
	}
	
	private interface EncryptionFunction {
		public void execute(EncryptionAlgorithm alg, File inputFile) throws IOException;
	}
	
	private interface DecryptionFunction {
		public void execute(EncryptionAlgorithm alg, File inputFile, Key key) throws IOException;
	}
	
	private class SyncEncryptionFunction implements EncryptionFunction {

		@Override
		public void execute(EncryptionAlgorithm alg, File inputFile) throws IOException {
			$.executeEncyption(alg, inputFile);
		}
	}
	
	private class AsyncEncryptionFunction implements EncryptionFunction {

		@Override
		public void execute(EncryptionAlgorithm alg, File inputFile)
				throws IOException {
			$.executeEncryptionAsync(alg, inputFile);
		}
	}
	
	private class SyncDecryptionFunction implements DecryptionFunction {

		@Override
		public void execute(EncryptionAlgorithm alg, File inputFile,Key key) throws IOException {
			$.executeDecryption(alg, inputFile,key);
		}
	}
	
	private class AsyncDecryptionFunction implements DecryptionFunction {

		@Override
		public void execute(EncryptionAlgorithm alg, File inputFile,Key key)
				throws IOException {
			$.executeDecryptionAsync(alg, inputFile,key);
		}
	}

	/*@Test
	public void printKeyToUserOutputStreamOnEncrypt() throws IOException {
		PipedInputStream is = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(is);
		PrintStream userOutput = new PrintStream(pos);
		File inputFile = folder.newFile("file1.txt");
		$.encrypt(inputFile);
		assertTrue(is.available()>0);
		is.close();
		userOutput.close();
	}
	
	@Test
	public void writesEveryEncryptedByteToNewFile() throws IOException {
		File inputFile = folder.newFile("file1.txt");

		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] plainText = new byte[]{10,20,30,40,50};

		os.write(plainText);
		os.close();

		$.executeEncyption(mockAlgorithm, inputFile);

		File encyptedFile = new File(inputFile.getPath()+".encrypted");
		FileInputStream fis = new FileInputStream(encyptedFile);
		int available = fis.available();
		fis.close();
		assertEquals(plainText.length, available);
	}
	
	@Test
	public void appliesDecryptionFunctionToEveryByteInInputFile() throws IOException {
		File inputFile = folder.newFile("file1.txt.encrypted");

		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] cypheredText = new byte[]{10,20,30,40,50};

		os.write(cypheredText);
		os.close();

		Key key = SingleValueKey.generate();
		Mockito.doReturn(key).when(mockAlgorithm).generateKey();

		$.executeDecryption(mockAlgorithm, inputFile, key);
		for(int i=0;i<cypheredText.length;i++) {
			Mockito.verify(mockAlgorithm).decrypt(cypheredText[i], new SingleValueKey((byte)0));
		}
	}
	
		@Test
	public void writeEveryDecryptedByteToNewDecryptedFile() throws IOException {
		File inputFile = folder.newFile("file1.txt.encrypted");

		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] cypheredText = new byte[]{10,20,30,40,50};

		os.write(cypheredText);
		os.close();

		$.executeDecryption(mockAlgorithm,inputFile, new SingleValueKey((byte)0));

		File decryptedFile = new File(inputFile.getParentFile().getPath()+"\\file1_decrypted.txt");
		FileInputStream fis = new FileInputStream(decryptedFile);
		int available = fis.available();
		fis.close();
		assertEquals(cypheredText.length,available);
	}
	
	@Test
	public void appliesEncryptionFunctionToEveryByteInInputFIle() throws IOException {
		File inputFile = folder.newFile("file1.txt");
		FileOutputStream os = new FileOutputStream(inputFile);
		byte[] plainText = new byte[]{10,20,30,40,50};

		os.write(plainText);

		Key key = SingleValueKey.generate();
		Mockito.doReturn(key).when(mockAlgorithm).generateKey();

		$.executeEncyption(mockAlgorithm,inputFile);

		for(int i=0;i<plainText.length;i++) {
			Mockito.verify(mockAlgorithm).encrypt(plainText[i], key);
		}
		inputFile.delete();
		os.close();

	}
	*/
	
	private void createsEncryptedFileWithCorrectAppendix(EncryptionFunction ef) throws IOException {
		ByteArrayOutputStream userOutput = new ByteArrayOutputStream(1);
		File inputFile = folder.newFile("file1.txt");
		File resultFile = new File(inputFile+".encrypted");
		if(resultFile.exists()){
			resultFile.delete();
		}
		ef.execute(mockAlgorithm, inputFile);
		assertTrue(resultFile.exists());
		resultFile.delete();
		userOutput.close();
	}

	@Test (timeout = 1000)
	public void createsEncryptedFileWithCorrectAppendixWhenSync() throws IOException {
		createsEncryptedFileWithCorrectAppendix(new SyncEncryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void createsEncryptedFileWithCorrectAppendixWhenASync() throws IOException {
		createsEncryptedFileWithCorrectAppendix(new AsyncEncryptionFunction());
	}
	
	private void createsDecryptedFileWithCorrectAppendix(EncryptionFunction ef,DecryptionFunction df) throws IOException {
		File inputFile = folder.newFile("file1.txt");

		ef.execute(mockAlgorithm, inputFile);
		Key key = SingleValueKey.generate();
		Mockito.doReturn(key).when(mockAlgorithm).generateKey();

		df.execute(mockAlgorithm, new File(inputFile.getPath()+".encrypted"), key);
		File decrypted = new File(inputFile.getParentFile().getPath()+"\\file1_decrypted.txt");
		assertTrue(decrypted.exists());
	}

	@Test (timeout = 1000)
	public void createsDecryptedFileWithCorrectAppendixWhenAsync() throws IOException {
		createsDecryptedFileWithCorrectAppendix(new AsyncEncryptionFunction(), new AsyncDecryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void createsDecryptedFileWithCorrectAppendixWhenSync() throws IOException {
		createsDecryptedFileWithCorrectAppendix(new SyncEncryptionFunction(), new SyncDecryptionFunction());
	}

	public void whenEncryptionFolderCreatesSubFolderWithCorrectName(EncryptionFunction ef) throws IOException {
		File targetDir = folder.newFolder();
		File encryptedDir = new File(targetDir.getPath()+"/encrypted");
		if(encryptedDir.exists())
			encryptedDir.delete();
		ef.execute(mockAlgorithm, targetDir);
		assertTrue(encryptedDir.exists());
	}
	
	@Test (timeout = 1000)
	public void whenEncryptionFolderCreatesSubFolderWithCorrectNameWhenSync() throws IOException {
		whenEncryptionFolderCreatesSubFolderWithCorrectName(new SyncEncryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void whenEncryptionFolderCreatesSubFolderWithCorrectNameWhenASync() throws IOException {
		whenEncryptionFolderCreatesSubFolderWithCorrectName(new AsyncEncryptionFunction());
	}
	
	private void whenDecryptionFolderCreatesSubFolderWithCorrectNameInParentDirectory(DecryptionFunction df) throws IOException {
		File targetDir = folder.newFolder();
		File encryptedSubFolder = new File(targetDir.getPath()+"\\encrypted");
		encryptedSubFolder.mkdir();
		File decryptedSubFolder = new File(targetDir.getPath()+"\\decrypted");
		if(decryptedSubFolder.exists())
			decryptedSubFolder.delete();
		df.execute(mockAlgorithm, encryptedSubFolder, SingleValueKey.generate());

		assertTrue(new File(targetDir.getPath()+"\\decrypted").exists());
	}

	@Test (timeout = 1000)
	public void whenDecryptionFolderCreatesSubFolderWithCorrectNameInParentDirectoryWhenSync() throws IOException {
		whenDecryptionFolderCreatesSubFolderWithCorrectNameInParentDirectory(new SyncDecryptionFunction());

	}
	
	@Test (timeout = 1000)
	public void whenDecryptionFolderCreatesSubFolderWithCorrectNameInParentDirectoryWhenAsync() throws IOException {
		whenDecryptionFolderCreatesSubFolderWithCorrectNameInParentDirectory(new AsyncDecryptionFunction());

	}
	
	private void everyFileIsEncryptedWhenEncryptingFolder(EncryptionFunction ef) throws IOException {
		File targetDir = folder.newFolder();
		for(int i=0;i<10;i++) {
			new File(targetDir.getPath()+"/file_"+i).createNewFile();
		}
		File encryptedFolder = new File(targetDir.getPath()+"/encrypted");
		if(encryptedFolder.exists())
			encryptedFolder.delete();
		ef.execute(mockAlgorithm, targetDir);
		assertTrue(encryptedFolder.exists());
		for(int i=0;i<10;i++) {
			assertTrue(new File(encryptedFolder.getPath()+"/file_"+i).exists());
		}
	}
	
	@Test (timeout = 1000)
	public void everyFileIsEncryptedWhenEncryptingFolderWhenSync() throws IOException {
		everyFileIsEncryptedWhenEncryptingFolder(new SyncEncryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void everyFileIsEncryptedWhenEncryptingFolderWhenAsync() throws IOException {
		everyFileIsEncryptedWhenEncryptingFolder(new AsyncEncryptionFunction());
	}
	
	private void everyFileIsDecryptedWhenDecryptingFolder(DecryptionFunction df) throws IOException {
		File targetDir = folder.newFolder();
		File encryptedFolder = new File(targetDir.getPath()+"/encrypted");
		encryptedFolder.mkdir();
		for(int i=0;i<10;i++) {
			new File(encryptedFolder.getPath()+"/file_"+i).createNewFile();
		}
		File decryptedFolder = new File(targetDir.getPath()+"/decrypted");
		if(decryptedFolder.exists())
			decryptedFolder.delete();
		df.execute(mockAlgorithm, encryptedFolder,SingleValueKey.generate());
		assertTrue(new File(targetDir.getPath()+"/decrypted").exists());
		for(int i=0;i<10;i++) {
			assertTrue(new File(decryptedFolder.getPath()+"/file_"+i).exists());
		}
	}

	@Test (timeout = 1000)
	public void everyFileIsDecryptedWhenDecryptingFolderWhenSync() throws IOException {
		everyFileIsDecryptedWhenDecryptingFolder(new SyncDecryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void everyFileIsDecryptedWhenDecryptingFolderWhenAsync() throws IOException {
		everyFileIsDecryptedWhenDecryptingFolder(new AsyncDecryptionFunction());
	}

	private void createsKeyFileInEncryptedFolder(EncryptionFunction ef) throws IOException {
		File targetDir = folder.newFolder();

		ef.execute(mockAlgorithm, targetDir);

		assertTrue(new File(targetDir.getPath()+"/key.bin").exists());
	}
	
	@Test (timeout = 1000)
	public void createsKeyFileInEncryptedFolderWhenSync() throws IOException {
		createsKeyFileInEncryptedFolder(new SyncEncryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void createsKeyFileInEncryptedFolderWhenAsync() throws IOException {
		createsKeyFileInEncryptedFolder(new AsyncEncryptionFunction());
	}
	
	private void createsReportsFileInEncryptedFolder(EncryptionFunction ef) throws IOException {
		File targetDir = folder.newFolder();

		ef.execute(mockAlgorithm, targetDir);

		assertTrue(new File(targetDir.getPath()+"/reports.xml").exists());
	}

	@Test (timeout = 1000)
	public void createsReportsFileInEncryptedFolderWhenSync() throws IOException {
		createsReportsFileInEncryptedFolder(new SyncEncryptionFunction());
	}
	
	@Test (timeout = 1000)
	public void createsReportsFileInEncryptedFolderWhenAsync() throws IOException {
		createsReportsFileInEncryptedFolder(new AsyncEncryptionFunction());
	}

}
