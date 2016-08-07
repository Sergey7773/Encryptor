package fileGeneration;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		int amount = Integer.parseInt(args[0]);
		String outputDir = args[1];
		int minSize = Integer.parseInt(args[2]);
		int maxSize = Integer.parseInt(args[3]);
		
		try {
			new FileGenerator().generateFiles(amount, outputDir, minSize, maxSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
