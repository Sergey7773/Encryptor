package encryptor.encryptor.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * servers as a WriteJob object for LoggedWriteJobPerformer
 * @author Sergey
 *
 */
@AllArgsConstructor
public class AsyncJob {
	@Getter @Setter private File file;
	@Getter @Setter private FileInputStream fileInputStream;
	@Getter @Setter private InputStream inputStream;


}
