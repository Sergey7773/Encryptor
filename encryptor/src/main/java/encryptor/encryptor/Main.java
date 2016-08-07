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

import org.apache.log4j.BasicConfigurator;

import com.google.inject.Guice;

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


public class Main {


	private static final String ALGORITHM_INDEX_REQUEST_STRING = 
			"Please enter the index of the desired algorithm (starting at 0).";
	private static final String FILEPATH_REQUEST_STRING = 
			"Please enter the file you wish to %s";

	private static final String actionRequestString = 
			"Enter desired action, %s for encryption and %s for decryption";
	private static final String encryptionAction = "ENC";
	private static final String decryptionAction = "DEC";

	private static final String BAD_PARAMS_STRING = 
			"Bad input\nEnter <-e|-l|-c> <filepath> <ENCRYPT|DECRYPT>";
	private static final String BAD_FILE = 
			"Incorrect filepath, enter filepath again";


	private static String[] algorithms = new String[] {"caesar,xor,mul,double,reverse,split"}; 
	public enum Action {ENCRYPT,DECRYPT};

	private static UserDialogHandler dialogHandler;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		BasicConfigurator.configure();
		dialogHandler = new ConsolelUserDialogHandler();
		Key key = null;
		EncryptionAlgorithm alg=null;
		dialogHandler.writeLine(actionRequestString, encryptionAction, decryptionAction);
		Action action = parseActionParam(dialogHandler.readLine());

		if(action.equals(Action.DECRYPT)) {
			dialogHandler.writeLine("Please provide the decryption key file");
			File keyFile = parseFilepathFromCMD(); 			
			key = readKeyFromFile(keyFile);
		}

		dialogHandler.writeLine(FILEPATH_REQUEST_STRING,
				(action.equals(Action.ENCRYPT)? "encrypt" : "decrypt"));
		File file = parseFilepathFromCMD();

		dialogHandler.writeLine("Would you like to load the last saved encryption algorithm? (y/n)");
		String response = dialogHandler.readLine();
		if(response.equals("y")) {
			alg = Utils.unmarshallEncryptionAlgorithm(Main.class.getClassLoader().getResource("alg.xml").getPath());
		} else {
			dialogHandler.writeLine("Would you like to import the algorithm from an xml configuration file? (y/n)");
			response = dialogHandler.readLine();
			if(response.equals("y")) {
				dialogHandler.writeLine("Please enter the filepath of the configuration file");
				alg = Utils.unmarshallEncryptionAlgorithm(dialogHandler.readLine());
			} else {
				dialogHandler.writeLine(ALGORITHM_INDEX_REQUEST_STRING
						+ algorithms.toString());
				alg = parseAlgorithmSelection(dialogHandler.readLine());
				
				dialogHandler.writeLine("Would you like to export this algorithm to an xml configuration file? (y/n)");
				response = dialogHandler.readLine();
				if(response.equals("y")) {
					Utils.marshallEncryptionAlgorithm(alg, dialogHandler.readLine());
				} 
			}
		}
		dialogHandler.writeLine("Would you like to use async mode? (y/n)");
		String useAsync = dialogHandler.readLine();
		
		EncryptionAlgorithmExecutor executor = 
				Guice.createInjector(new DefaultEncryptorInjector()).getInstance(EncryptionAlgorithmExecutor.class);
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



	private static void onBadParams() {
		System.err.println(BAD_PARAMS_STRING);
		throw new IllegalArgumentException();
	}

	private static void onBadFile() {
		System.err.println(BAD_FILE);
	}

	private static Action parseActionParam(String action) {
		String toCompare = action.replace("\n", "");
		toCompare = toCompare.replace("\r", "");
		if(toCompare.equals(decryptionAction)) return Action.DECRYPT;
		else if(toCompare.equals(encryptionAction)) return Action.ENCRYPT;
		onBadParams();  //exit
		return null;
	}

	private static File parseFilepathFromCMD() {
		File $ = new File(dialogHandler.readLine());
		while($==null || $.exists()==false) {
			onBadFile();
			$ = new File(dialogHandler.readLine());
		}
		return $;
	}

	private static EncryptionAlgorithm parseAlgorithmSelection(String algorithmIndex) {
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

	private static EncryptionAlgorithm parseAlgorithmSelectionRecursive(int index, int depth) {
		switch(index) {
		case 3: return parseBiArgAlgorithm(depth);

		case 4: return parseSingleArgAlgorithm(4,depth+1);
		case 5: return parseSingleArgAlgorithm(5,depth+1);
		}
		return null;

	}

	private static EncryptionAlgorithm parseBiArgAlgorithm(int depth) {
		dialogHandler.writeLine("Please Enter the index of the first algorithm.");
		EncryptionAlgorithm first = parseAlgorithmSelectionRecursive(
				Integer.parseInt(dialogHandler.readLine()), depth+1);
		dialogHandler.writeLine("please Enter the index of the second algorithm");
		EncryptionAlgorithm second = parseAlgorithmSelectionRecursive(
				Integer.parseInt(dialogHandler.readLine()), depth+1);
		return new DoubleAlgorithm(first, second);
	}

	private static EncryptionAlgorithm parseSingleArgAlgorithm(int index, int depth) {
		dialogHandler.writeLine("Please enter the index of the nested algorithm");
		EncryptionAlgorithm nested = parseAlgorithmSelectionRecursive(
				Integer.parseInt(dialogHandler.readLine()), depth+1);
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
}
