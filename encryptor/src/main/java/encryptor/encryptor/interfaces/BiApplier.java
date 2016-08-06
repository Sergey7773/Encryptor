package encryptor.encryptor.interfaces;

public interface BiApplier<R,T,U> {
	public R apply(T t, U u);
}
