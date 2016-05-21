package encryptor.encryptor;

import java.io.File;
import java.util.Scanner;

public class InputParser {
	private static final String enterParams = "-e";
	private static final String loadParams = "-l";
	private static final String changeSavedParams = "-c";
	
	private static final String BAD_PARAMS_STRING = 
			"Bad input\nEnter <-e|-l|-c> <filepath> <ENCRYPT|DECRYPT>";
	private static final String BAD_FILE = "Incorrect filepath, enter filepath again";
	
	public enum ParamsMode {ENTER_NEW,LOAD,CHANGE_SAVED};
	public enum Action {ENCRYPT,DECRYPT};
	
	
	private void onBadParams() {
		System.err.println(BAD_PARAMS_STRING);
		throw new IllegalArgumentException();
	}
	
	private void onBadFile() {
		System.err.println(BAD_FILE);
	}
	
	public ParamsMode parseParamsMode(String paramsMode) {
		if(paramsMode.equals(changeSavedParams)) return ParamsMode.CHANGE_SAVED;
		else if(paramsMode.equals(enterParams)) return ParamsMode.ENTER_NEW;
		else if(paramsMode.equals(loadParams)) return ParamsMode.LOAD;
		onBadParams();
		return null;
	}
	
	public Action parseActionParam(String action) {
		if(action.equals("DECRYPT")) return Action.DECRYPT;
		else if(action.equals("ENCRYPT")) return Action.ENCRYPT;
		onBadParams();  //exit
		return null;
	}
	
	public File parseFile(String filename) {
		File f = new File(filename);
		if(!f.exists()) 
		{
			onBadFile();
			return null;
		}
		return f;
	}
	
	
}
