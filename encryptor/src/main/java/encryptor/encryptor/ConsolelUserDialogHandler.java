package encryptor.encryptor;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;

import encryptor.encryptor.interfaces.UserDialogHandler;

/**
 * Used as the default UserDialogHandler by the EncryptorApplication.
 * @author Sergey
 *
 */
public class ConsolelUserDialogHandler implements UserDialogHandler {

	private Console console;
	
	public ConsolelUserDialogHandler() {
		console = System.console();
	}
	
	@Override
	/**
	 * writes the given string to a console if available, and to System.out otherwise.
	 */
	public void writeLine(String line) {
		if(console!=null)
			console.format(line+"\r\n");
		else
			System.out.println(line);
		
	}

	@Override
	/**
	 * reads a line from the console if available, and from System.in otherwise
	 */
	public String readLine() {
		if(console!=null) 
			return console.readLine();
		else {
			int readBytes=0;
			byte[] buffer = new byte[500];
			try {
				readBytes = System.in.read(buffer, 0, buffer.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String res = new String(Arrays.copyOf(buffer, readBytes)).replaceAll("(\r)(\n)", "");
			return res;
		}
	}

	@Override
	/**
	 * prints a formatted string to a console if available, and to System.out otherwise.
	 */
	public void writeLine(String line, Object... args) {
		this.writeLine(String.format(line, args));
	}

	@Override
	public void writeError(String line) {
		if(System.console()==null) {
			System.err.println(line);
		} else {
			System.console().format(line);
		}
		
	}

}
