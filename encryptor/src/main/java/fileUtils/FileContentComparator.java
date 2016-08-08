package fileUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class FileContentComparator {
	
	private static final int BUFFER_SIZE = 500;
	
	public boolean isContentEqual(String filepath1, String filepath2) throws IOException {
		FileInputStream fis1 = new FileInputStream(filepath1);
		FileInputStream fis2 = new FileInputStream(filepath2);
		byte[] buffer1 = new byte[BUFFER_SIZE];
		byte[] buffer2 = new byte[BUFFER_SIZE];
		
		int read1=0,read2=0;
		while(fis1.available()>0 && fis2.available()>0) {
			read1 = fis1.read(buffer1);
			read2 = fis2.read(buffer2);
			if(read1!=read2) {
				return cleanAndReturn(fis1,fis2,false);
			}
			for(int i=0;i<read1;i++) {
				if(buffer1[i]!=buffer2[i]) return cleanAndReturn(fis1,fis2,false);
			}
		}	
		return cleanAndReturn(fis1, fis2, true);
	}
	
	private boolean cleanAndReturn(FileInputStream fis1,FileInputStream fis2, boolean result) throws IOException {
		try{
			fis1.close();
		} catch(Exception e) {
			e.printStackTrace();
		} 
		try {
			fis2.close();
		} catch(Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
}
