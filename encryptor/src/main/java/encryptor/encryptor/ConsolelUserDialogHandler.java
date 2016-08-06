package encryptor.encryptor;

import java.io.Console;
import java.io.IOException;

import encryptor.encryptor.interfaces.UserDialogHandler;

public class ConsolelUserDialogHandler implements UserDialogHandler {

	private Console console;
	
	public ConsolelUserDialogHandler() {
		console = System.console();
	}
	
	@Override
	public void writeLine(String line) {
		if(console!=null)
			console.format(line+"\n");
		else
			System.out.println(line+"\n");
		
	}

	@Override
	public String readLine() {
		if(console!=null) 
			return console.readLine();
		else {
			byte[] buffer = new byte[500];
			try {
				System.in.read(buffer, 0, buffer.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return String.valueOf(buffer);
		}
	}

	@Override
	public void writeLine(String line, Object... args) {
		this.writeLine(String.format(line, args));
		
	}

}
