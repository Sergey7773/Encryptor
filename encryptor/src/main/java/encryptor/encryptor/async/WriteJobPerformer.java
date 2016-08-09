package encryptor.encryptor.async;

public interface WriteJobPerformer<T, S> {
	public T perform(S s);
}
