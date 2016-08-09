package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import com.google.inject.Inject;

import encryptor.encryptor.algorithms.CaesarEncryptionAlgorithm;
import encryptor.encryptor.algorithms.DoubleAlgorithm;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.algorithms.MultiplicationEncryptionAlgorithm;
import encryptor.encryptor.algorithms.ReverseAlgorithm;
import encryptor.encryptor.algorithms.SplitAlgorithm;
import encryptor.encryptor.algorithms.XorEncryptionAlgorithm;
import encryptor.encryptor.interfaces.Key;
import encryptor.encryptor.interfaces.UserDialogHandler;
import encryptor.encryptor.xml.Utils;
import encryptor.encryptor.xml.XmlParser;

public class EncryptorApplication {
	public static final String SAVE_ALGORITHM_OPTION_STRING = "Would you like to export this algorithm to an xml configuration file? (y/n)";

	private static String[] algorithms = new String[] {"caesar","xor","mul","double","reverse","split"}; 
	
	public static final String KEY_FILE_REQUEST_STRING = "Please provide the decryption key file";
	public static final String ALGORITHM_INDEX_REQUEST_STRING = 
			"Please enter the index of the desired algorithm (starting at 0)."+"\n"+Arrays.toString(algorithms);
	public static final String FILEPATH_REQUEST_STRING = 
			"Please enter the file you wish to %s";

	public static final String actionRequestString = 
			"Enter desired action, %s for encryption and %s for decryption";
	public static final String encryptionAction = "ENC";
	public static final String decryptionAction = "DEC";

	public static final String BAD_PARAMS_STRING = 
			"Illegal arguments, please enter ENC or DEC";
	public static final String BAD_FILE = 
			"Incorrect filepath, enter filepath again";
	
	

	private UserDialogHandler dialogHandler;
	private EncryptionAlgorithmExecutor executor;
	private XmlParser xmlParser;
	
	@Inject
	public EncryptorApplication(UserDialogHandler dialogHandler,
			EncryptionAlgorithmExecutor executor,
			XmlParser parser) {
		this.dialogHandler = dialogHandler;
		this.executor = executor;
		this.xmlParser = parser;
	}
	
	/**
	 * Starts the application and the dialog with the user.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void run() throws IOException, ClassNotFoundException {
		Key key = null;
		EncryptionAlgorithm alg=null;
		dialogHandler.writeLine(actionRequestString, encryptionAction, decryptionAction);
		Action action = parseActionParam(dialogHandler.readLine());
		if(action==null) return;
		if(action.equals(Action.DECRYPT)) {
			dialogHandler.writeLine(KEY_FILE_REQUEST_STRING);
			File keyFile = parseFilepathFromCMD(); 			
			key = readKeyFromFile(keyFile);
		}

		dialogHandler.writeLine(FILEPATH_REQUEST_STRING,
				(action.equals(Action.ENCRYPT)? "encrypt" : "decrypt"));
		File file = parseFilepathFromCMD();

		dialogHandler.writeLine("Would you like to load the last saved encryption algorithm? (y/n)");
		String response = dialogHandler.readLine();
		if(response.equals("y")) {
			alg = xmlParser.unmarshallEncryptionAlgorithm(Main.class.getClassLoader().getResource("alg.xml").getPath());
		} else {
			dialogHandler.writeLine("Would you like to import the algorithm from an xml configuration file? (y/n)");
			response = dialogHandler.readLine();
			if(response.equals("y")) {
				dialogHandler.writeLine("Please enter the filepath of the configuration file");
				alg = xmlParser.unmarshallEncryptionAlgorithm(dialogHandler.readLine());
			} else {
				dialogHandler.writeLine(ALGORITHM_INDEX_REQUEST_STRING);
				alg = parseAlgorithmSelection(dialogHandler.readLine());
				
				dialogHandler.writeLine(SAVE_ALGORITHM_OPTION_STRING);
				response = dialogHandler.readLine();
				if(response.equals("y")) {
					xmlParser.marshallEncryptionAlgorithm(alg, dialogHandler.readLine());
				} 
			}
		}
		dialogHandler.writeLine("Would you like to use async mode? (y/n)");
		String useAsync = dialogHandler.readLine();
		
		if(useAsync.equals("y")) {
			if(action.equals(Action.ENCRYPT))
				executor.executeEncryptionAsync(alg, file);
			else
				executor.executeDecryptionAsync(alg, file, key);
		} else {
			if(action.equals(Action.ENCRYPT)) 
				executor.executeEncyption(alg, file);
			else
				executor.executeDecryption(alg, file, key);
		}
	}
	
	
	private void onBadParams() {
		//dialogHandler.writeLine(BAD_PARAMS_STRING);
		System.err.println(BAD_PARAMS_STRING);
		//throw new IllegalArgumentException();
	}

	private void onBadFile() {
		dialogHandler.writeLine(BAD_FILE);
		System.err.println(BAD_FILE);
	}

	private Action parseActionParam(String action) {
		String toCompare = action.replace("\n", "");
		toCompare = toCompare.replace("\r", "");
		if(toCompare.equals(decryptionAction)) return Action.DECRYPT;
		else if(toCompare.equals(encryptionAction)) return Action.ENCRYPT;
		onBadParams();  //exit
		return null;
	}

	private File parseFilepathFromCMD() {
		File $ = new File(dialogHandler.readLine());
		while($==null || $.exists()==false) {
			onBadFile();
			$ = new File(dialogHandler.readLine());
		}
		return $;
	}

	private EncryptionAlgorithm parseAlgorithmSelection(String algorithmIndex) {
		int index=Integer.parseInt(algorithmIndex);
		if(index<0 || index>=algorithms.length) {
			throw new IllegalArgumentException();
		}
		switch(index) {
		case 0: return new CaesarEncryptionAlgorithm();
		case 1: return new XorEncryptionAlgorithm();
		case 2: return new MultiplicationEncryptionAlgorithm();
		default: return parseAlgorithmSelectionRecursive(index,1);
		}
	}

	private EncryptionAlgorithm parseAlgorithmSelectionRecursive(int index, int depth) {
		switch(index) {
		case 3: return parseBiArgAlgorithm(depth);

		case 4: return parseSingleArgAlgorithm(4,depth+1);
		case 5: return parseSingleArgAlgorithm(5,depth+1);
		}
		return null;

	}

	private EncryptionAlgorithm parseBiArgAlgorithm(int depth) {
		dialogHandler.writeLine("Please Enter the index of the first algorithm.");
		EncryptionAlgorithm first = parseAlgorithmSelectionRecursive(
				Integer.parseInt(dialogHandler.readLine()), depth+1);
		dialogHandler.writeLine("please Enter the index of the second algorithm");
		EncryptionAlgorithm second = parseAlgorithmSelectionRecursive(
				Integer.parseInt(dialogHandler.readLine()), depth+1);
		return new DoubleAlgorithm(first, second);
	}

	private EncryptionAlgorithm parseSingleArgAlgorithm(int index, int depth) {
		dialogHandler.writeLine("Please enter the index of the nested algorithm");
		EncryptionAlgorithm nested = parseAlgorithmSelectionRecursive(
				Integer.parseInt(dialogHandler.readLine()), depth+1);
		switch(index) {
		case 4: return new ReverseAlgorithm(nested);
		case 5: return new SplitAlgorithm(nested);
		}
		return null;
	}

	private Key readKeyFromFile(File f) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Key $ = (Key)ois.readObject();
		ois.close();
		return $;
	}
}
