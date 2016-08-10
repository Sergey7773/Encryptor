package encryptor.encryptor.algorithms;

import java.util.Arrays;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.ToString;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import encryptor.encryptor.CompositeKey;
import encryptor.encryptor.SingleValueKey;
import encryptor.encryptor.algorithms.appliers.ActionApplierFactory;
import encryptor.encryptor.interfaces.Key;

@XmlRootElement
@XmlType(name = "MultiplicationEncryptionAlgorithm")
@ToString(exclude = {"lastKey","decKey"})
public class MultiplicationEncryptionAlgorithm extends EncryptionAlgorithm{
	
	private byte lastKey;
	private byte decKey;
	
	@Inject
	public MultiplicationEncryptionAlgorithm(
			@Named("encryptionApplierFactory")ActionApplierFactory encryptionApplierFactory, 
			@Named("decryptionApplierFactory")ActionApplierFactory decryptionApplierFactory) {
		super(encryptionApplierFactory,decryptionApplierFactory);
	}
	
	public MultiplicationEncryptionAlgorithm() {
		lastKey=1;
		decKey=1;
	}
	
	/**
	 * returns the value of the key, multiplied with overflow by the given value
	 */
	public byte encrypt(byte value, Key key) {
		if(!isValidKey(key))
			throw new IllegalArgumentException();
		byte valueOfKey = ((SingleValueKey)key).getValue();
		return MWO(value,valueOfKey);
	}

	/**
	 * returns the value of the decryption key (a number x s.t. MWO(x,key)==1 holds)),
	 *  multiplied with overflow by the given value
	 */
	public byte decrypt(byte value, Key key) {
		if(!isValidKey(key))
			throw new IllegalArgumentException();
		byte valueOfKey = ((SingleValueKey)key).getValue();
		if(lastKey!=valueOfKey) {
			lastKey=valueOfKey;
			decKey = findNewDecKey();
		}
		return MWO(value,decKey);
	}
	
	private byte MWO(byte val1,byte val2) {
		return (byte)(val1*val2);
	}
	
	private byte findNewDecKey() {
		for(byte k = Byte.MIN_VALUE; k<=Byte.MAX_VALUE; k++) {
			if(MWO(k,lastKey)==(byte)1)
				return k;
		}
		return -1;
	}

	/**
	 * returns true if the given key is of type SingleValueKey and its value is not 0 or divided by 2 
	 * without a remainder.
	 */
	public boolean isValidKey(Key key) {
		byte valueOfKey = ((SingleValueKey)key).getValue();
		if(valueOfKey == 0 || valueOfKey % 2 == 0 ) return false;
		return true;
	}

	@Override
	/**
	 * returns a SingleValueKey whose value is not 0 or divided by 2 without a remainder.
	 */
	public Key generateKey() {
		return SingleValueKey.generate(new Predicate<Byte>() {
			
			@Override
			public boolean test(Byte t) {
				return t==0 || t%2==0;
			}
		});
	}

}
