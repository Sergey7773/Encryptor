package encryptor.encryptor;

import java.io.Serializable;

import encryptor.encryptor.interfaces.Key;

/**
 * A key composed of two other keys.
 * @author Sergey
 *
 */
public class CompositeKey implements Key, Serializable {

	
	private static final long serialVersionUID = 2040477221440359777L;
	
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
