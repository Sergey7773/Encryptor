package encryptor.encryptor;

import lombok.NonNull;

import org.apache.log4j.Logger;

public class LoggingUtils {
	public static final String ACTION_FAILURE_LOGGING_MESSAGE =
			"%s of %s finished ended with the following exception %s";
	public static final String ACTION_SUCCESS_LOGGING_MESSAGE = 
			"%s of %s finished successfully.\n"+
			"The action took %d seconds";
	public static final String ACTION_START_LOGGING_MESSAGE =
			"%s of %s started.";
	
	/**
	 * creates a logger with 'className' and logs a message at INFO level, saying when the encryption\decryption
	 * of the file at 'filepath' started.
	 * @param className
	 * @param actionType
	 * @param filepath
	 */
	public static void writeActionStart(String className, Action actionType, String filepath) {
		Logger logger = Logger.getLogger(className);
		logger.info(String.format(ACTION_START_LOGGING_MESSAGE,
						actionType.toString(),filepath));
	}
	
	/**
	 * creates a logger with 'className' and logs a message at INFO level, saying how long the encryption\decryption
	 * of the file at 'filepath' took
	 * @param className
	 * @param actionType
	 * @param seconds
	 * @param filepath
	 */
	public static void writeActionFinisedWithSuccess(String className, Action actionType,
			String filepath, int seconds) {
		Logger logger = Logger.getLogger(className);
		logger.info(String.format(ACTION_SUCCESS_LOGGING_MESSAGE,
						actionType.toString(),filepath,seconds));
	}
	
	/**
	 * creates a logger with 'className' and logs a message at INFO level, saying why the encryption\decryption 
	 * of the file at 'filepath' failed (which exception was thrown).
	 * @param className
	 * @param actionType
	 * @param filepath
	 * @param e
	 */
	public static void writeActionFinishedWithFailure(String className, Action actionType,
			String filepath, Exception e) {
		Logger logger = Logger.getLogger(className);
		logger.info(String.format(ACTION_FAILURE_LOGGING_MESSAGE,
						actionType.toString(),filepath,e.getClass().getName()));
	}
}
