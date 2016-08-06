package report;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "failureReport")
public class FailureReport implements Report {
	
	private Status status;
	private String exceptionName;
	private String exceptionMessage;
	private String stackTrace;
	
	public FailureReport() {
		status = Status.FAILURE;
		exceptionMessage = exceptionName = stackTrace = null;
	}
	
	public Status getStatus() {
		return status;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	
}
