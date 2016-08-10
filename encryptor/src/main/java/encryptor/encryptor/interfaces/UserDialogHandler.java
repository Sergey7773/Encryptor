package encryptor.encryptor.interfaces;

public interface UserDialogHandler {
	public void writeLine(String line);
	public void writeLine(String line, Object... args);
	public String readLine();
	public void writeError(String line);
}
