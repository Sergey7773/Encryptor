package encryptor.encryptor;

import java.util.List;
import java.util.Random;

public class SingleValueKey implements Key {
	
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
	
	public static SingleValueKey generate(List<Byte> illegalValues) {
		Random rnd = new Random(System.currentTimeMillis());
		byte[] buffer = new byte[1];
		do {
			rnd.nextBytes(buffer);
		} while(!illegalValues.contains(buffer[0]));
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