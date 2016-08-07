package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AsyncJob {
	private InputStream is;
	private FileInputStream fis;
	private File file;

	public AsyncJob(File f,FileInputStream fis, InputStream is) {
		this.file = f;
		this.is = is;
		this.fis = fis;
	}
	
	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public FileInputStream getFis() {
		return fis;
	}

	public void setFis(FileInputStream fis) {
		this.fis = fis;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
