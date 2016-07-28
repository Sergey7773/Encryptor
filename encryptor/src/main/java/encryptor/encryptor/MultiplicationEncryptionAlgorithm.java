package encryptor.encryptor;

public class MultiplicationEncryptionAlgorithm implements EncryptionAlgorithm{
	
	private byte lastKey;
	private byte decKey;
	
	public MultiplicationEncryptionAlgorithm() {
		lastKey=1;
		decKey=1;
	}
	
	public byte encrypt(byte value, byte key) {
		return MWO(value,key);
	}

	public byte decrypt(byte value, byte key) {
		if(lastKey!=key) {
			lastKey=key;
			decKey = findNewDecKey();
		}
		return MWO(value,decKey);
	}
	
	private byte MWO(byte val1,byte val2) {
		return (byte)(val1*val2);
	}
	
	private byte findNewDecKey() {
		for(byte k = Byte.MIN_VALUE; k<=Byte.MAX_VALUE; k++) {
			if(MWO(k,lastKey)==1)
				return k;
		}
		return -1;
	}

}
