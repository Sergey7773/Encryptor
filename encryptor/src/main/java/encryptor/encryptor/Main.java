package encryptor.encryptor;

import java.io.Console;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import encryptor.encryptor.algorithms.CaesarEncryptionAlgorithm;
import encryptor.encryptor.algorithms.DoubleAlgorithm;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;
import encryptor.encryptor.algorithms.MultiplicationEncryptionAlgorithm;
import encryptor.encryptor.algorithms.ReverseAlgorithm;
import encryptor.encryptor.algorithms.SplitAlgorithm;
import encryptor.encryptor.algorithms.xorEncryptionAlgorithm;


public class Main {


	private static final String ALGORITHM_INDEX_REQUEST_STRING = 
			"Please enter the index of the desired algorithm (starting at 0).\n";
	private static final String FILEPATH_REQUEST_STRING = 
			"Please enter the file you wish to %s";
	private static final String enterParams = "-e";
	private static final String loadParams = "-l";
	private static final String changeSavedParams = "-c";

	private static final String actionRequestString = 
			"Enter desired action, %s for encryption and %s for decryption";
	private static final String encryptionAction = "ENC";
	private static final String decryptionAction = "DEC";

	private static final String BAD_PARAMS_STRING = 
			"Bad input\nEnter <-e|-l|-c> <filepath> <ENCRYPT|DECRYPT>";
	private static final String BAD_FILE = 
			"Incorrect filepath, enter filepath again";

	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";

	private static String[] algorithms = new String[] {"caesar,xor,mul"}; 

	private enum ParamsMode {ENTER_NEW,LOAD,CHANGE_SAVED};
	private enum Action {ENCRYPT,DECRYPT};

	private static Console console;

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		console = System.console();
		//ParamsMode paramsMode =  parseParamsMode(args[0]); //for future use
		Key key = null;
		console.format(actionRequestString, encryptionAction, decryptionAction);
		Action action = parseActionParam(console.readLine());

		if(action.equals(Action.DECRYPT)) {
			console.format("Please provide the decryption key file");
			File keyFile = parseFilepathFromCMD(); 			
			key = readKeyFromFile(keyFile);
		}

		console.format(FILEPATH_REQUEST_STRING,
				(action.equals(Action.ENCRYPT)? "encrypt" : "decrypt"));
		File file = parseFilepathFromCMD();

		console.format(ALGORITHM_INDEX_REQUEST_STRING
				+ algorithms.toString());
		EncryptionAlgorithm alg = parseAlgorithmSelection(console.readLine());

		if(file.isFile())
			performActionOnSingleFile(alg,file,key,action);
		else 
			performActionOnDirectory(alg, file, key, action);
	}

	private static void performActionOnSingleFile(EncryptionAlgorithm alg,
			File inputFile,Key key, Action actionType) throws FileNotFoundException {
		String outputFilePath = (actionType.equals(Action.ENCRYPT)) ?
				appedEncryptedToFilename(inputFile) : appedDecryptedToFilename(inputFile);
		FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
		FileInputStream fis = new FileInputStream(inputFile);
		
		try {
			if(actionType.equals(Action.ENCRYPT)) {
				alg.encrypt(fis,fos);
			} else {
				alg.decrypt(fis,fos,key);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static void performActionOnDirectory(EncryptionAlgorithm alg,
			File inputDir, Key key, Action actionType) throws FileNotFoundException {
		String outputDirPath = (actionType.equals(Action.ENCRYPT)) ?
				inputDir.getPath()+"/encrypted" : inputDir.getPath() + "/decrypted";
		File outputDir = new File(outputDirPath);
		File[] filesInDir = inputDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return !pathname.isDirectory();
			}
		});
		for(int i=0;i<filesInDir.length;i++) {
			FileInputStream fis = new FileInputStream(filesInDir[i]);
			FileOutputStream fos = new FileOutputStream(
					new File(outputDir.getPath()+"/"+filesInDir[i].getName()));
			try {
				if(actionType.equals(Action.ENCRYPT)) {
					alg.encrypt(fis,fos);
				} else {
					alg.decrypt(fis,fos,key);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

	private static void onBadParams() {
		System.err.println(BAD_PARAMS_STRING);
		throw new IllegalArgumentException();
	}

	private static void onBadFile() {
		System.err.println(BAD_FILE);
	}

	private static ParamsMode parseParamsMode(String paramsMode) {
		if(paramsMode.equals(changeSavedParams)) return ParamsMode.CHANGE_SAVED;
		else if(paramsMode.equals(enterParams)) return ParamsMode.ENTER_NEW;
		else if(paramsMode.equals(loadParams)) return ParamsMode.LOAD;
		onBadParams();
		return null;
	}

	private static Action parseActionParam(String action) {
		if(action.equals(decryptionAction)) return Action.DECRYPT;
		else if(action.equals(encryptionAction)) return Action.ENCRYPT;
		onBadParams();  //exit
		return null;
	}

	private static File parseFilepathFromCMD() {
		File $ = new File(console.readLine());
		while($==null || $.exists()==false) {
			onBadFile();
			$ = new File(console.readLine());
		}
		return $;
	}

	private static EncryptionAlgorithm parseAlgorithmSelection(String algorithmIndex) {
		int index=Integer.parseInt(algorithmIndex);
		try {
			index = System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(index<0 || index>=algorithms.length) {
			throw new IllegalArgumentException();
		}
		switch(index) {
		case 0: return new CaesarEncryptionAlgorithm();
		case 1: return new xorEncryptionAlgorithm();
		case 2: return new MultiplicationEncryptionAlgorithm();
		default: return parseAlgorithmSelectionRecursive(index,1);
		}
	}

	private static EncryptionAlgorithm parseAlgorithmSelectionRecursive(int index, int depth) {
		switch(index) {
		case 3: return parseBiArgAlgorithm(depth);

		case 4: return parseSingleArgAlgorithm(4,depth+1);
		case 5: return parseSingleArgAlgorithm(5,depth+1);
		}
		return null;

	}

	private static EncryptionAlgorithm parseBiArgAlgorithm(int depth) {
		console.format("Please Enter the index of the first algorithm.");
		EncryptionAlgorithm first = parseAlgorithmSelectionRecursive(
				Integer.parseInt(console.readLine()), depth+1);
		console.format("please Enter the index of the second algorithm");
		EncryptionAlgorithm second = parseAlgorithmSelectionRecursive(
				Integer.parseInt(console.readLine()), depth+1);
		return new DoubleAlgorithm(first, second);
	}

	private static EncryptionAlgorithm parseSingleArgAlgorithm(int index, int depth) {
		console.format("Please enter the index of the nested algorithm");
		EncryptionAlgorithm nested = parseAlgorithmSelectionRecursive(
				Integer.parseInt(console.readLine()), depth+1);
		switch(index) {
		case 4: return new ReverseAlgorithm(nested);
		case 5: return new SplitAlgorithm(nested);
		}
		return null;
	}

	private static Key readKeyFromFile(File f) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Key $ = (Key)ois.readObject();
		ois.close();
		return $;
	}

	private static String appedEncryptedToFilename(File f) {
		return f.getPath()+ENCRYPTED_FORMAT;
	}

	private static String appedDecryptedToFilename(File f) {
		String $ = f.getPath();
		$=$.replace(ENCRYPTED_FORMAT, "");
		int lastDot = $.lastIndexOf('.');
		if(lastDot==-1) return $+DECRYPTED_EXTENTION;
		$=$.substring(0, lastDot)+DECRYPTED_EXTENTION+$.substring(lastDot, $.length());
		return $;
	}
}
