package encryptor.encryptor;

public class ActionObserver implements Observer {
	
	private long startTime;
	
	private String onActionStartMessage;
	private String onActionEndMessage;
	
	public ActionObserver(String onStartMessage, String onEndMessage) {
		this.onActionStartMessage = onStartMessage;
		this.onActionEndMessage = onEndMessage;
	}
	
	public void onStart() {
		System.out.println(onActionStartMessage);
		startTime = System.currentTimeMillis();
	}

	public void onEnd() {
		System.out.println(onActionEndMessage);
		System.out.println("The action took "+
				(System.currentTimeMillis()-startTime)/1000 + " seconds");
	}
}
