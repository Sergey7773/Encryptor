package reports;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "failureReport")
public class FailureReport extends Report {
	
	private Status status;
	private String exceptionName;
	private String exceptionMessage;
	private String stackTrace;
	private String filename;
	
	@XmlElement
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filaname) {
		this.filename = filaname;
	}

	public FailureReport() {
		status = Status.FAILURE;
		exceptionMessage = exceptionName = stackTrace = null;
	}
	
	@XmlElement
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status=status;
	}

	@XmlElement
	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
	
	@XmlElement
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	@XmlElement
	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	
}
