package encryptor.encryptor.algorithms.appliers;

import encryptor.encryptor.interfaces.Key;

public interface ActionApplier {
	public Byte apply(Byte value, Key key);
}
