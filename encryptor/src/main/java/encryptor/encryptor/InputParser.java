package encryptor.encryptor;

import java.io.File;
import java.util.Scanner;

public class InputParser {
	private static final String enterParams = "-s";
	private static final String loadParams = "-l";
	private static final String changeSavedParams = "-c";
	
	private static final String BAD_PARAMS_STRING = 
			"Bad input\n Enter <-s|-l|-c> <filepath> <ENCRYPT|DECRYPT>";
	private static final String BAD_FILE = "Incorrect filepath, enter filepath again";
	
	public enum ParamsMode {ENTER_NEW,LOAD,CHANGE_SAVED};
	public enum Action {ENCRYPT,DECRYPT};
	
	
	private void onBadParams() {
		System.out.println(BAD_PARAMS_STRING);
		System.exit(1);
	}
	
	private void onBadFile() {
		System.out.println(BAD_FILE);
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
		Scanner s = new Scanner(System.in);
		while(!f.exists()) {
			onBadFile();
			filename = s.nextLine();
			f = new File(filename);
		}
		s.close();
		return f;
	}
	
	
}
