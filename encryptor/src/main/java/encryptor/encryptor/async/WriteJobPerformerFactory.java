package encryptor.encryptor.async;

public interface WriteJobPerformerFactory<T,S> {
	public WriteJobPerformer<T, S> get();
}
