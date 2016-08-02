package encryptor.encryptor;


public class CompositeKey implements Key {
	private Key first,second;
	
	public CompositeKey(Key first,Key second) {
		this.first=first;
		this.second=second;
	}
	
	public Key getFirstKey() {
		return this.first;
	}
	
	public Key getSecondKey() {
		return this.second;
	}
}
