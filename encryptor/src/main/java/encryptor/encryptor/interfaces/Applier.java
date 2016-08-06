package encryptor.encryptor.interfaces;

public interface Applier<T,U> {
	T apply(U val);
}
