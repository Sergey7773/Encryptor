package encryptor.encryptor.interfaces;

public class Pair<T,S> {
	public T first;
	public S second;
	
	public Pair(T t, S s) {
		this.first= t;
		this.second = s;
	}
	
	public T getFirst() {
		return first;
	}
	
	public S getSecond() {
		return second;
	}
}
