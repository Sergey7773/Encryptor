package acceptanceTests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import dependencyInjection.DefaultEncryptionAlgorithmExecutorModule;
import encryptor.encryptor.ConsolelUserDialogHandler;
import encryptor.encryptor.EncryptionAlgorithmExecutor;
import encryptor.encryptor.EncryptorApplication;
import encryptor.encryptor.interfaces.UserDialogHandler;
import encryptor.encryptor.xml.XmlParser;
import fileUtils.FileContentComparator;
import fileUtils.FileGenerator;

@RunWith(Parameterized.class)
public class AcceptanceTestsOutputCorrectness {
	
	private UserDialogHandler dialogProvider;
	private static int testNum;
	private List<String> providedInput;
	private static boolean singleFileAsserts;
	private static File[] testFolders;
	private static int NUM_OF_TESTS=30;
	
	@ClassRule
	public static TemporaryFolder testFolderRoot = new TemporaryFolder();
	
	public AcceptanceTestsOutputCorrectness(int tn,boolean sfa, List<String> pi) {
		this.providedInput = pi;
		singleFileAsserts = sfa;
		testNum = tn;
		this.dialogProvider = new UserDialogHandler() {
			private int index =0;
			@Override
			public void writeLine(String line, Object... args) {	
			}
			
			@Override
			public void writeLine(String line) {	
			}
			
			@Override
			public String readLine() {
				index+=1;
				return providedInput.get(index-1);
			}
		};
	}
	
	private static void initDirs() throws IOException {
		testFolders = new File[NUM_OF_TESTS];
		FileGenerator filegen = new FileGenerator();
		for(int i=0;i<NUM_OF_TESTS;i++) {
			testFolders[i] = testFolderRoot.newFolder("testFolder_"+i);
			filegen.generateSmallFiles(5, testFolders[i].getPath(), 1,10);
		}
	}
	
	@AfterClass
	public static void cleanup() {
		testFolderRoot.delete();
	}
	private static Object[] makeInputArgsLoadDefaultAlg(int testNum,boolean singleFileAsserts,String async) {
		return makeInputArgs(testNum, singleFileAsserts, async, Arrays.asList("y"));
	}
	
	private static Object[] makeInputArgsCustomAlg(int testNum, boolean singleFileAsserts,String async,
			List<String> algDescription) {
		List<String> algLoadIns = new ArrayList<String>();
		algLoadIns.addAll(Arrays.asList("n","n"));
		algLoadIns.addAll(algDescription);
		algLoadIns.add("n");
		return makeInputArgs(testNum,singleFileAsserts,async,algLoadIns);
	}
	
	private static Object[] makeInputArgs(int testNum, boolean singleFileAsserts,String async,
			List<String> algDescription) {
		List<String> args = new ArrayList<String>();
		String inputFile = testFolderPath(testNum);
		String encryptedFile = testFolderPath(testNum);
		String keyFile = testFolderPath(testNum)+"/key.bin";
		if(singleFileAsserts) {
			inputFile = inputFile+"/randomFile_1";
			encryptedFile = encryptedFile+"/randomFile_1.encrypted";
		} else {
			encryptedFile = encryptedFile+"/encrypted";
		}
		
		args.addAll(Arrays.asList("ENC",inputFile,"y",async,"y","DEC",keyFile,encryptedFile,"y",async,"n"));
		args.addAll(algDescription);
		args.addAll(Arrays.asList(async,"y","DEC",keyFile,encryptedFile));
		args.addAll(algDescription);
		args.addAll(Arrays.asList(async,"n"));
		return new Object[] {testNum,singleFileAsserts,args};
	}
	
	private static String testFolderPath(int i) {
		return testFolders[i].getPath();
	}
	
	@Parameters
	public static Collection<Object[]> data() throws IOException {
		testFolderRoot.create();
		initDirs();
		ArrayList<Object[]> inputs = new ArrayList<Object[]>();
		inputs.add(makeInputArgsLoadDefaultAlg(0, true, "n"));
		inputs.add(makeInputArgsLoadDefaultAlg(1, true, "y"));
		inputs.add(makeInputArgsCustomAlg(2, true, "n", Arrays.asList("0")));
		inputs.add(makeInputArgsCustomAlg(3, true, "y", Arrays.asList("0")));
		inputs.add(makeInputArgsCustomAlg(4, true, "n", Arrays.asList("1")));
		inputs.add(makeInputArgsCustomAlg(5, true, "y", Arrays.asList("1")));
		inputs.add(makeInputArgsCustomAlg(6, true, "n", Arrays.asList("2")));
		inputs.add(makeInputArgsCustomAlg(7, true, "y", Arrays.asList("2")));
		inputs.add(makeInputArgsCustomAlg(8, true, "n", Arrays.asList("3","0","1")));
		inputs.add(makeInputArgsCustomAlg(9, true, "y", Arrays.asList("3","0","1")));
		inputs.add(makeInputArgsCustomAlg(10, true, "n", Arrays.asList("4","3","0","1")));
		inputs.add(makeInputArgsCustomAlg(11, true, "y", Arrays.asList("4","3","0","1")));
		inputs.add(makeInputArgsCustomAlg(12, true, "n", Arrays.asList("5","4","3","0","1")));
		inputs.add(makeInputArgsCustomAlg(13, true, "y", Arrays.asList("5","4","3","0","1")));
		inputs.add(makeInputArgsLoadDefaultAlg(14, false, "n"));
		inputs.add(makeInputArgsLoadDefaultAlg(15, false, "y"));
		inputs.add(makeInputArgsCustomAlg(16, false, "n", Arrays.asList("0")));
		inputs.add(makeInputArgsCustomAlg(17, false, "y", Arrays.asList("0")));
		inputs.add(makeInputArgsCustomAlg(18, false, "n", Arrays.asList("1")));
		inputs.add(makeInputArgsCustomAlg(19, false, "y", Arrays.asList("1")));
		inputs.add(makeInputArgsCustomAlg(20, false, "n", Arrays.asList("2")));
		inputs.add(makeInputArgsCustomAlg(21, false, "y", Arrays.asList("2")));
		inputs.add(makeInputArgsCustomAlg(22, false, "n", Arrays.asList("3","0","1")));
		inputs.add(makeInputArgsCustomAlg(23, false, "y", Arrays.asList("3","0","1")));
		inputs.add(makeInputArgsCustomAlg(24, false, "n", Arrays.asList("4","3","0","1")));
		inputs.add(makeInputArgsCustomAlg(25, false, "y", Arrays.asList("4","3","0","1")));
		inputs.add(makeInputArgsCustomAlg(26, false, "n", Arrays.asList("5","4","3","0","1")));
		inputs.add(makeInputArgsCustomAlg(27, false, "y", Arrays.asList("5","4","3","0","1")));
		return inputs;
	}
	
	@Test
	public void encryptThenDecryptDoesNotChangeFile() throws ClassNotFoundException, IOException {
		EncryptorApplication $ = Guice.createInjector(new AbstractModule() {
			
			@Override
			protected void configure() {
				bind(UserDialogHandler.class).toInstance(dialogProvider);
				bind(EncryptionAlgorithmExecutor.class).toInstance(
						Guice.createInjector(new DefaultEncryptionAlgorithmExecutorModule()).
						getInstance(EncryptionAlgorithmExecutor.class));
				bind(XmlParser.class).toInstance(new XmlParser());
				
			}
		}).getInstance(EncryptorApplication.class);
		
		$.run();
		
		if(singleFileAsserts) {
			assertTrue(new FileContentComparator().isContentEqual(testFolders[testNum].getPath()+"/randomFile_1",
					testFolders[testNum].getPath()+"/randomFile_1_decrypted"));
		} else {
			for(int i=0;i<5;i++) {
				assertTrue(new FileContentComparator()
				.isContentEqual(testFolders[testNum].getPath()+"/randomFile_"+i,
						testFolders[testNum].getPath()+"/decrypted/randomFile_"+i));
			}
		}
	}
}
