package encryptor.encryptor.async;

public interface WriteJobFactory<T, S> {
	public T make(S readJob);
}
