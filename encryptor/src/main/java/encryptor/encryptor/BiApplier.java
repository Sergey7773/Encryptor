package encryptor.encryptor;

public interface BiApplier<R,T,U> {
	public R apply(T t, U u);
}
