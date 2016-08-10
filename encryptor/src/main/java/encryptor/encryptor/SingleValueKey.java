package encryptor.encryptor;

import java.io.Serializable;
import java.util.Random;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import encryptor.encryptor.interfaces.Key;

/**
 * A key which holds a single byte value
 * @author Sergey
 *
 */
@AllArgsConstructor
public class SingleValueKey implements Key, Serializable {
	

	private static final long serialVersionUID = -6300576333626322453L;
	private byte value;
	
	/**
	 * 
	 * @return a SingleValueKey with a random value (from MIN_VALUE to MAX_VALUE of Byte)
	 */
	public static SingleValueKey generate() {
		Random rnd = new Random(System.currentTimeMillis());
		byte[] buffer = new byte[1];
		rnd.nextBytes(buffer);
		return new SingleValueKey(buffer[0]);
	}
	
	/**
	 * generates keys with random value and test them against the predicate.
	 * @param illegalValuesPredicate
	 * @return a key with value v for which illegalValuesPredicate.test(v)==false holds.
	 */
	public static SingleValueKey generate(Predicate<Byte> illegalValuesPredicate) {
		Random rnd = new Random(System.currentTimeMillis());
		byte[] buffer = new byte[1];
		do {
			rnd.nextBytes(buffer);
		} while(illegalValuesPredicate.test(buffer[0]));
		return new SingleValueKey(buffer[0]);
	}
	
	/**
	 * 
	 * @return the value of this key
	 */
	public byte getValue() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SingleValueKey)) return false;
		SingleValueKey tmp = (SingleValueKey)o;
		return tmp.getValue()==this.value;
		
	}
}
