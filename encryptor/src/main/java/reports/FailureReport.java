package reports;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@XmlType(name = "failureReport")
public class FailureReport extends Report {
	
	@Getter @Setter private String filename;
	@Getter @Setter private Status status;
	@Getter @Setter private String exceptionName;
	@Getter @Setter private String exceptionMessage;
	@Getter @Setter private String stackTrace;

	public FailureReport() {
		status = Status.FAILURE;
		exceptionMessage = exceptionName = stackTrace = null;
	}
	
	public FailureReport(String filename, Exception e ) {
		status = Status.FAILURE;
		exceptionMessage = e.getMessage();
		stackTrace = e.getStackTrace().toString();
		exceptionName = e.getClass().getName();
		this.filename = filename;
	}
}
