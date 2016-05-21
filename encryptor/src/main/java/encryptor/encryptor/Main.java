package encryptor.encryptor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import encryptor.encryptor.InputParser.Action;
import encryptor.encryptor.InputParser.ParamsMode;


public class Main {

	public static void main(String[] args) {

		InputParser parser = new InputParser();
		ParamsMode paramsMode =  parser.parseParamsMode(args[0]); //for future use
		Action action =  parser.parseActionParam(args[1]);
		String filepath = args[2];
		File file=parser.parseFile(filepath);
		while(file==null) {
			Scanner s = new Scanner(System.in);
			filepath = s.nextLine();
			s.close();
			file = parser.parseFile(filepath);
		}
		
		
		try {
			if(action.equals(Action.ENCRYPT)) {
				new SimulationEncryptor().encrypt(file);
			} else {
				new SimulationDecryptor().Decrypt(file);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

	}
}
