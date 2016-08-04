package encryptor.encryptor;

public class Pair<T,S> {
	T first;
	S second;
	
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
