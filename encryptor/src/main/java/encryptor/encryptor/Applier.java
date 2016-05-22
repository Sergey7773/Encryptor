package encryptor.encryptor;

public interface Applier<T,U> {
	T apply(U val);
}
