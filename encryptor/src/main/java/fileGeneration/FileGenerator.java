package fileGeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FileGenerator {

	private static int MEGA = (int) Math.pow(10, 6);

	/**
	 * generates <i>amount</i> files in <i>outputDir</i>, with sizes ranging from <i>minSize</i> to <i>maxSize</i>
	 * @param amount
	 * @param outputDir
	 * @param minSize minimum size in MB.
	 * @param maxSize maximum size in MB
	 * @throws IOException 
	 */
	public void generateFiles(int amount, String outputDir, int minSize, int maxSize) throws IOException {
		Random rndGen = new Random(System.currentTimeMillis());
		for(int i=0;i<amount;i++) {
			int size = rndGen.nextInt((maxSize-minSize)*MEGA) + minSize*MEGA;
			String filepath = outputDir+"/randomFile_"+i;
			generateFile(size, filepath);
		}
	}

	private void generateFile(int size, String filepath) throws IOException {
		File outputFile = new File(filepath);
		FileOutputStream fos = new FileOutputStream(outputFile);
		Random rndGen = new Random(System.currentTimeMillis());
		byte[] buffer = new byte[2000];
		for(int i=0;i<size;i+=buffer.length) {
			rndGen.nextBytes(buffer);
			fos.write(buffer, i*buffer.length, buffer.length);
		}
		fos.close();
	}

}
