package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class AlgorithmWrapper {
	
	private EncryptionAlgorithm m_encryptionAlgorithm;
	
	private static final String ENCRYPTED_FORMAT = ".encrypted";
	private static final String DECRYPTED_EXTENTION = "_decrypted";
	
	public AlgorithmWrapper(EncryptionAlgorithm algorithm) {
		m_encryptionAlgorithm = algorithm;
	}
	
	private String appedEncryptedToFilename(File f) {
		return f.getPath()+ENCRYPTED_FORMAT;
	}
	
	private String appedDecryptedToFilename(File f) {
		String $ = f.getPath();
		$=$.replace(ENCRYPTED_FORMAT, "");
		int lastDot = $.lastIndexOf('.');
		if(lastDot==-1) return $+DECRYPTED_EXTENTION;
		$=$.substring(0, lastDot)+DECRYPTED_EXTENTION+$.substring(lastDot, $.length());
		return $;
	}
	
	public void decrypt(File f,OutputStream userOutputStream, byte key) throws IOException {
		File outputFile = new File(appedDecryptedToFilename(f));
		doAction(f, outputFile, userOutputStream, new DecryptionApplier(m_encryptionAlgorithm, key));
	}
	
	public void encrypt(File f,OutputStream userOutputStream) throws IOException {
		byte[] key = new byte[1];
		new Random().nextBytes(key);
		userOutputStream.write(key);
		File outputFile = new File(appedEncryptedToFilename(f));
		doAction(f, outputFile, userOutputStream, new EncryptionApplier(m_encryptionAlgorithm, key[0]));
	}
	
	private void doAction(File f,File outputFile,OutputStream userOutputStream,Applier<Byte,Byte> function) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		byte plain[] = new byte[1];
		byte cyphered[] = new byte[1];
		while(fis.available()>0) {
			fis.read(plain);
			cyphered[0]=function.apply(plain[0]);
			fos.write(cyphered);
		}
		
		fis.close();
		fos.close();
	}
}
