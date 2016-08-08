package encryptor.encryptor;

import java.io.Console;
import java.io.IOException;
import java.util.Arrays;

import encryptor.encryptor.interfaces.UserDialogHandler;

public class ConsolelUserDialogHandler implements UserDialogHandler {

	private Console console;
	
	public ConsolelUserDialogHandler() {
		console = System.console();
	}
	
	@Override
	public void writeLine(String line) {
		if(console!=null)
			console.format(line+"\r\n");
		else
			System.out.println(line);
		
	}

	@Override
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
	public void writeLine(String line, Object... args) {
		this.writeLine(String.format(line, args));
	}

}
