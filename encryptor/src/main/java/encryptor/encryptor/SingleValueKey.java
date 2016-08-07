package encryptor.encryptor;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import encryptor.encryptor.interfaces.Key;

public class SingleValueKey implements Key, Serializable {
	

	private static final long serialVersionUID = -6300576333626322453L;
	private byte value;
	
	public SingleValueKey(byte value) {
		this.value=value;
	}
	
	public static SingleValueKey generate() {
		Random rnd = new Random(System.currentTimeMillis());
		byte[] buffer = new byte[1];
		rnd.nextBytes(buffer);
		return new SingleValueKey(buffer[0]);
	}
	
	public static SingleValueKey generate(Predicate<Byte> illegalValuesPredicate) {
		Random rnd = new Random(System.currentTimeMillis());
		byte[] buffer = new byte[1];
		do {
			rnd.nextBytes(buffer);
		} while(illegalValuesPredicate.test(buffer[0]));
		return new SingleValueKey(buffer[0]);
	}
	
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
