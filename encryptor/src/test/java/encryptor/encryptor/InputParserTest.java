package encryptor.encryptor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import encryptor.encryptor.InputParser.Action;
import encryptor.encryptor.InputParser.ParamsMode;

public class InputParserTest {

	private InputParser $;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before
	public void setup() {
		$ = new InputParser();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionOnInvalidAction() {
		$.parseActionParam("action");
	}
	
	@Test
	public void returnActionMatchingDecrypt() {
		assertEquals(Action.DECRYPT,$.parseActionParam("DECRYPT"));
	}
	
	@Test
	public void returnActionMatchingEncrypt() {
		assertEquals(Action.ENCRYPT,$.parseActionParam("ENCRYPT"));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void throwIllegalArgumentExceptionOnInvalidParamsMode() {
		$.parseParamsMode("params");
	}
	
	@Test
	public void returnMatchingParamsModeOnValidInput() {
		assertEquals(ParamsMode.CHANGE_SAVED, $.parseParamsMode("-c"));
		assertEquals(ParamsMode.ENTER_NEW,$.parseParamsMode("-e"));
		assertEquals(ParamsMode.LOAD,$.parseParamsMode("-l"));
	}
	
	@Test
	public void returnNullOnInvalidFilepath() throws IOException {
		assertNull($.parseFile(folder.newFile().getPath()+"_wrong"));
	}
	
	@Test
	public void returnFileOnCorrectFilepath() throws IOException {
		File tmpFile = folder.newFile();
		assertEquals(tmpFile,$.parseFile(tmpFile.getPath()));
	}
	
}
