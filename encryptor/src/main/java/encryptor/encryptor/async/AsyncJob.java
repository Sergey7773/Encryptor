package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * servers as a WriteJob object for LoggedWriteJobPerformer
 * @author Sergey
 *
 */
public class AsyncJob {
	private InputStream is;
	private FileInputStream fis;
	private File file;

	public AsyncJob(File f,FileInputStream fis, InputStream is) {
		this.file = f;
		this.is = is;
		this.fis = fis;
	}
	
	public InputStream getInputStream() {
		return is;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}

	public FileInputStream getFileInputStream() {
		return fis;
	}

	public void setFileInputStream(FileInputStream fis) {
		this.fis = fis;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}
