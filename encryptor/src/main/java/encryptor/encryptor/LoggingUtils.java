package encryptor.encryptor;

import org.apache.log4j.Logger;

import encryptor.encryptor.Main.Action;

public class LoggingUtils {
	public static final String ACTION_FAILURE_LOGGING_MESSAGE = "%s of %s finished ended with the following exception %s";
	public static final String ACTION_SUCCESS_LOGGING_MESSAGE = "%s of %s finished successfully.\n"+
			"The action took %d seconds";
	public static final String ACTION_START_LOGGING_MESSAGE = "%s of %s started.";
	
	public static void writeActionStart(String className,Action actionType, String filepath) {
		Logger logger = Logger.getLogger(className);
		logger.info(String.format(ACTION_START_LOGGING_MESSAGE,
						actionType.toString(),filepath));
	}
	
	public static void writeActionFinisedWithSuccess(String className, Action actionType,
			String filepath, int seconds) {
		Logger logger = Logger.getLogger(className);
		logger.info(String.format(ACTION_SUCCESS_LOGGING_MESSAGE,
						actionType.toString(),filepath,seconds));
	}
	
	public static void writeActionFinishedWithFailure(String className, Action actionType,
			String filepath, Exception e) {
		Logger logger = Logger.getLogger(className);
		logger.info(String.format(ACTION_FAILURE_LOGGING_MESSAGE,
						actionType.toString(),filepath,e.getClass().getName()));
	}
}
