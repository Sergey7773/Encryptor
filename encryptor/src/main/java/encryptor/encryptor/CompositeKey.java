package encryptor.encryptor;


public class CompositeKey {
	private Key first,second;
	
	public CompositeKey(Key first,Key second) {
		this.first=first;
		this.second=second;
	}
	
	public Key getFirstKey() {
		return this.first;
	}
	
	public Key getSeconKey() {
		return this.second;
	}
}
