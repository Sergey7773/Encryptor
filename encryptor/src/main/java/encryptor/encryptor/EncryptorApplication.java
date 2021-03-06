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
import encryptor.encryptor.xml.XmlParser;

public class EncryptorApplication {
	public static final String ASYNC_MODE_OPTION = "Would you like to use async mode? (y/n)";

	public static final String FILEPATH_FOR_CONF_FILE_PROMPT = "Please enter the filepath of the configuration file";

	public static final String IMPORT_FROM_FILE = "Would you like to import the algorithm from an xml configuration file? (y/n)";

	public static final String LOAD_DEFAULT_OPTION = "Would you like to load the default encryption algorithm? (y/n)";

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
		do {
			Key key = null;
			EncryptionAlgorithm alg=null;
			boolean saveAlg = false;
			String saveAlgToFilepath ="";
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

			dialogHandler.writeLine(LOAD_DEFAULT_OPTION);
			if(parseYesNoAnswer()) {
				alg = xmlParser.unmarshallEncryptionAlgorithm(
						Main.class.getClassLoader().getResource("alg.xml").getPath());
			} else {
				dialogHandler.writeLine(IMPORT_FROM_FILE);
				if(parseYesNoAnswer()) {
					dialogHandler.writeLine(FILEPATH_FOR_CONF_FILE_PROMPT);
					alg = xmlParser.unmarshallEncryptionAlgorithm(dialogHandler.readLine());
				} else {
					dialogHandler.writeLine(ALGORITHM_INDEX_REQUEST_STRING);
					alg = parseAlgorithm();
					dialogHandler.writeLine(SAVE_ALGORITHM_OPTION_STRING);
					if(parseYesNoAnswer()) {
						saveAlg = true;	
						dialogHandler.writeLine("Please enter the filepath");
						saveAlgToFilepath = dialogHandler.readLine();
					} 
				}
			}
			if(alg!=null)
				dialogHandler.writeLine(alg.toString());
			dialogHandler.writeLine(ASYNC_MODE_OPTION);
			if(parseYesNoAnswer()) {
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
			if(saveAlg) {
				xmlParser.marshallEncryptionAlgorithm(alg, saveAlgToFilepath);
				saveAlg = false;
				saveAlgToFilepath = "";
			}
			dialogHandler.writeLine("would you like to do another action?");
		} while(parseYesNoAnswer());
	}

	private void onBadFile() {
		dialogHandler.writeError(BAD_FILE);
		//System.err.println(BAD_FILE);
	}

	private Action parseActionParam(String action) {
		String toCompare = action.replace("\n", "");
		toCompare = toCompare.replace("\r", "");
		if(toCompare.equals(decryptionAction)) return Action.DECRYPT;
		else if(toCompare.equals(encryptionAction)) return Action.ENCRYPT;
		dialogHandler.writeError(BAD_PARAMS_STRING);
		throw new IllegalArgumentException(BAD_PARAMS_STRING);
	}

	private File parseFilepathFromCMD() {
		File $ = new File(dialogHandler.readLine());
		while($==null || $.exists()==false) {
			onBadFile();
			$ = new File(dialogHandler.readLine());
		}
		return $;
	}

	private boolean parseYesNoAnswer() {
		String response = dialogHandler.readLine();
		if(!response.equals("y") && !response.equals("n")) {
			dialogHandler.writeError("answer must be 'y' or 'n'");
			throw new IllegalArgumentException("answer must be 'y' or 'n'");
		}
		return response.equals("y");
	}

	private EncryptionAlgorithm parseAlgorithmSelection(String algorithmIndex,
			int depth) {
		int index=Integer.parseInt(algorithmIndex);
		if(index<0 || index>=algorithms.length) {
			throw new IllegalArgumentException();
		}
		switch(index) {
		case 0: return new CaesarEncryptionAlgorithm();
		case 1: return new XorEncryptionAlgorithm();
		case 2: return new MultiplicationEncryptionAlgorithm();
		case 3: return new DoubleAlgorithm();
		case 4: return new ReverseAlgorithm();
		case 5:	return new SplitAlgorithm();
		default: return null;
		}
	}

	private EncryptionAlgorithm parseAlgorithm() {
		EncryptionAlgorithm root = parseAlgorithmSelection(dialogHandler.readLine(), 0);
		if(root instanceof DoubleAlgorithm || root instanceof ReverseAlgorithm || root instanceof SplitAlgorithm) {
			return parseAlgorithm(root, root,0);
		} 
		return root;
	}

	private EncryptionAlgorithm parseAlgorithm(EncryptionAlgorithm root, EncryptionAlgorithm parent,int depth) {
		dialogHandler.writeLine(root.toString());
		if(parent instanceof DoubleAlgorithm) {
			DoubleAlgorithm da = ((DoubleAlgorithm)parent);
			dialogHandler.writeLine("Depth: "+depth+". Please Enter the index of the second algorithm");
			EncryptionAlgorithm first = parseAlgorithmSelection(dialogHandler.readLine(),0);
			da.setFirstAlgorithm(first);
			parseAlgorithm(root, first,depth+1);
			dialogHandler.writeLine("Depth: "+depth+". Please Enter the index of the second algorithm");
			EncryptionAlgorithm second = parseAlgorithmSelection(dialogHandler.readLine(),0);
			da.setSecondAlgorithm(second);
			parseAlgorithm(root,second,depth+1);
		} else if(parent instanceof ReverseAlgorithm) {
			ReverseAlgorithm ra = ((ReverseAlgorithm)parent);
			dialogHandler.writeLine("Depth: "+depth+". Please enter the index of the nested algorithm");
			EncryptionAlgorithm nested = parseAlgorithmSelection(dialogHandler.readLine(), 0);
			ra.setNestedAlgorithm(nested);
			parseAlgorithm(root, nested,depth+1);
		} else if(parent instanceof SplitAlgorithm) {
			SplitAlgorithm ra = ((SplitAlgorithm)parent);
			dialogHandler.writeLine("Depth: "+depth+". Please enter the index of the nested algorithm");
			EncryptionAlgorithm nested = parseAlgorithmSelection(dialogHandler.readLine(), 0);
			ra.setNestedAlgorithm(nested);
			parseAlgorithm(root, nested,depth+1);
		}
		return parent;
	}


	private Key readKeyFromFile(File f) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Key $ = (Key)ois.readObject();
		ois.close();
		return $;
	}
}
