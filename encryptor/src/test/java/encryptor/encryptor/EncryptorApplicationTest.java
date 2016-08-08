package encryptor.encryptor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.UserDialogHandler;
import encryptor.encryptor.xml.XmlParser;

public class EncryptorApplicationTest {
	
	private UserDialogHandler dialogHandler;
	private EncryptionAlgorithmExecutor mockExecutor;
	private XmlParser mockParser;
	private EncryptorApplication $;
	private List<String> output;
	private List<String> input;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before
	public void setup() throws IOException {
		mockExecutor = Mockito.mock(EncryptionAlgorithmExecutor.class);
		mockParser = Mockito.mock(XmlParser.class);
		dialogHandler = new UserDialogHandler() {
			@Override
			public void writeLine(String line, Object... args) {
				this.writeLine(String.format(line, args));
				
			}
			
			@Override
			public void writeLine(String line) {
				output.add(line);
				
			}
			
			@Override
			public String readLine() {
				return input.remove(0);
			}
		};
		
		Injector injector = Guice.createInjector(new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(UserDialogHandler.class).toInstance(dialogHandler);
				bind(EncryptionAlgorithmExecutor.class).toInstance(mockExecutor);
				bind(XmlParser.class).toInstance(mockParser);
			}
		});
		
		$ = injector.getInstance(EncryptorApplication.class);
		
		input = new ArrayList<String>();
		output = new ArrayList<String>();
		
		folder.create();
	}
	
	@After
	public void cleanup() {
		folder.delete();
	}
	
	@Test
	public void printsErrorMessageWhenUsingWrongActionType() throws ClassNotFoundException, IOException {
		input.add("not ENC or DEC");
		$.run();
		assertFalse(output.size()==0);
		assertEquals(EncryptorApplication.BAD_PARAMS_STRING,output.get(1));
		
		input.add("152");
		$.run();
		assertFalse(output.size()==0);
		assertEquals(EncryptorApplication.BAD_PARAMS_STRING,output.get(1));
	}
	
	@Test 
	public void promptsForFileUntilLegalPathRecieved() throws IOException, ClassNotFoundException {
		
		File tmpFile = folder.newFile();
		Mockito.doNothing().when(mockExecutor).executeEncyption(Mockito.any(),Mockito.any());
		input.addAll(Arrays.asList("ENC","not a file","C:\\not a file",tmpFile.getPath(),"y","n"));
		$.run();
		
		
		
		assertEquals(EncryptorApplication.BAD_FILE,output.get(2));
		assertEquals(EncryptorApplication.BAD_FILE,output.get(3));
		
	}
	
	@Test 
	public void asksForKeyFileIfChoseDecription() throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		
		File tmpKeyFile = folder.newFile();
		Key key = SingleValueKey.generate();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpKeyFile));
		oos.writeObject(key);
		oos.close();
		
		input.addAll(Arrays.asList("DEC",tmpKeyFile.getPath(),tmpFile.getPath(),"y","n"));
		
		$.run();
		
		assertEquals(EncryptorApplication.KEY_FILE_REQUEST_STRING,output.get(1));
	}
	
	@Test
	public void unmarshallsAlgorithmConfigurationFileIfChoseToLoadLastSavedAlgorithm() 
			throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		input.addAll(Arrays.asList("ENC",tmpFile.getPath(),"y","n"));
		
		$.run();
		
		Mockito.verify(mockParser,Mockito.times(1)).unmarshallEncryptionAlgorithm(
				Main.class.getClassLoader().getResource("alg.xml").getPath());
	}
	
	@Test
	public void readsFromDifferentAlgorithmConfigurationFileIfRequested() throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		File algFile = folder.newFile();
		input.addAll(Arrays.asList("ENC",tmpFile.getPath(),"n","y",algFile.getPath(),"n"));
		
		$.run();
		
		Mockito.verify(mockParser).unmarshallEncryptionAlgorithm(algFile.getPath());
	}
	
	@Test
	public void promptsToEnterAlgorithmIfNotLoadingFromFile() throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		input.addAll(Arrays.asList("ENC",tmpFile.getPath(),"n","n","0","n","n"));
		
		$.run();
		
		assertEquals(EncryptorApplication.ALGORITHM_INDEX_REQUEST_STRING,output.get(4));
	}
	
	@Test
	public void suggestToSaveNewlyEnteredAlgorithm() throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		input.addAll(Arrays.asList("ENC",tmpFile.getPath(),"n","n","0","n","n"));
		
		$.run();
		
		assertEquals(EncryptorApplication.SAVE_ALGORITHM_OPTION_STRING,output.get(5));
	}
	
	@Test
	public void usesSyncronousFunctionsWhenRequested() throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		input.addAll(Arrays.asList("ENC",tmpFile.getPath(),"y","n"));
		EncryptionAlgorithm mockAlg = Mockito.mock(EncryptionAlgorithm.class);
		Mockito.doReturn(mockAlg).when(mockParser).unmarshallEncryptionAlgorithm(Mockito.anyString());
		$.run();
		Mockito.verify(mockExecutor).executeEncyption(Mockito.eq(mockAlg), Mockito.eq(tmpFile));
		
		File tmpKeyFile = folder.newFile();
		Key key = SingleValueKey.generate();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpKeyFile));
		oos.writeObject(key);
		oos.close();
		
		input.clear();
		input.addAll(Arrays.asList("DEC",tmpKeyFile.getPath(),tmpFile.getPath(),"y","n"));
		$.run();
		
		Mockito.verify(mockExecutor).executeDecryption(Mockito.eq(mockAlg), Mockito.eq(tmpFile), Mockito.eq(key));
	}
	
	@Test
	public void usesAsynchronousFunctionsWhenRequested() throws IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		input.addAll(Arrays.asList("ENC",tmpFile.getPath(),"y","y"));
		EncryptionAlgorithm mockAlg = Mockito.mock(EncryptionAlgorithm.class);
		Mockito.doReturn(mockAlg).when(mockParser).unmarshallEncryptionAlgorithm(Mockito.anyString());
		$.run();
		Mockito.verify(mockExecutor).executeEncryptionAsync(Mockito.eq(mockAlg), Mockito.eq(tmpFile));
		
		File tmpKeyFile = folder.newFile();
		Key key = SingleValueKey.generate();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpKeyFile));
		oos.writeObject(key);
		oos.close();
		
		input.clear();
		input.addAll(Arrays.asList("DEC",tmpKeyFile.getPath(),tmpFile.getPath(),"y","y"));
		$.run();
		
		Mockito.verify(mockExecutor).executeDecryptionAsync(Mockito.eq(mockAlg), Mockito.eq(tmpFile), Mockito.eq(key));
	}
	
	@Test
	public void promtsToEnterKeyFileUntilLegalPathGiven() throws FileNotFoundException, IOException, ClassNotFoundException {
		File tmpFile = folder.newFile();
		
		File tmpKeyFile = folder.newFile();
		Key key = SingleValueKey.generate();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpKeyFile));
		oos.writeObject(key);
		oos.close();
		
		input.addAll(Arrays.asList(
				"DEC","not a file","15","C:\\noFile",tmpKeyFile.getPath(),tmpFile.getPath(),"y","n"));
		
		$.run();
		for(int i=2;i<5;i++)
			assertEquals(EncryptorApplication.BAD_FILE,output.get(i));
		assertEquals(String.format(EncryptorApplication.FILEPATH_REQUEST_STRING,"decrypt"),output.get(5));
	}
}
