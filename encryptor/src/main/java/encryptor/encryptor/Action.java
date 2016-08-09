package encryptor.encryptor;

public enum Action {ENCRYPT,DECRYPT;
	@Override
	public String toString() {
		if(this.equals(ENCRYPT)) return "Encryption";
		return "Decryption";
	}
}