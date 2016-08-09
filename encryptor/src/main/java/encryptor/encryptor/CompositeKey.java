package encryptor.encryptor;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import encryptor.encryptor.interfaces.Key;

/**
 * A key composed of two other keys.
 * @author Sergey
 *
 */
@AllArgsConstructor
public class CompositeKey implements Key, Serializable {

	
	private static final long serialVersionUID = 2040477221440359777L;
	
	@Getter private Key firstKey,secondKey;
	
	
}
