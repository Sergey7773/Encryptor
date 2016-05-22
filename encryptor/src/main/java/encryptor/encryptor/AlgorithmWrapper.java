package encryptor.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AlgorithmWrapper {
	
	private EncryptionAlgorithm m_encryptionAlgorithm;
	
	public AlgorithmWrapper(EncryptionAlgorithm algorithm) {
		m_encryptionAlgorithm = algorithm;
	}
	
	public void decrypt(File f,OutputStream userOutputStream, byte key) throws IOException {
		doAction(f, userOutputStream, new EncryptionApplier(m_encryptionAlgorithm, key));
	}
	
	public void encrypt(File f,OutputStream userOutputStream) throws IOException {
		byte key =0;
		//TODO:generate random key
		doAction(f, userOutputStream, new DecryptionApplier(m_encryptionAlgorithm, key));
	}
	
	private void doAction(File f,OutputStream userOutputStream,Applier<Byte,Byte> function) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		//TODO: create file with correct name
		FileOutputStream fos = new FileOutputStream(new File(""));
		
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
