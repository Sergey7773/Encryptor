package encryptor.encryptor.algorithms.appliers;

import lombok.AllArgsConstructor;
import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.interfaces.Key;
@AllArgsConstructor
public class CompositeDecryptionApplier implements ActionApplier {
	private ActionApplier first;
	private ActionApplier second;
	
	@Override
	public Byte apply(Byte value, Key key) {
		if(!(key instanceof CompositeKey))
			throw new IllegalArgumentException();
		CompositeKey ck =((CompositeKey)key);
		return first.apply(second.apply(value, ck.getSecondKey()),ck.getFirstKey());
	}
}
