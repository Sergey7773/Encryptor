package encryptor.encryptor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Main {

	private static final String enterParams = "-s";
	private static final String loadParams = "-l";
	private static final String changeSavedParams = "-c";

	public static void main(String[] args) {
		String paramsMode = args[0];
		String filepath = args[1];
		String action = args[2];

		if(!paramsMode.matches("(-s)|(-l)|(-c)")) {
			System.out.println("Bad input");
			System.out.println("Enter <-s|-l|-c> <filepath> <ENCRYPT|DECRYPT>");
			System.exit(1);
		}

		if(!action.matches("(DECRYPT)|(ENCRYPT)")) {
			System.out.println("Bad input");
			System.out.println("Enter <-s|-l|-c> <filepath> <ENCRYPT|DECRYPT>");
			System.exit(1);
		}

		File f = new File(filepath);
		Scanner s = new Scanner(System.in);
		while(!f.exists()) {
			System.out.println("Incorrect filepath, enter filepath again");
			filepath = s.nextLine();
			f = new File(filepath);
		}
		s.close();
		try {
			if(action.equals("ENCRYPT")) {
				new SimulationEncryptor().encrypt(f);
			} else {
				new SimulationDecryptor().Decrypt(f);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

	}
}
