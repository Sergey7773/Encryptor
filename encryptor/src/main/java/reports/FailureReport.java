package reports;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@XmlType(name = "failureReport", propOrder={"filename","status","exceptionName","exceptionMessage","stackTrace"})
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
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		status = Status.FAILURE;
		exceptionMessage = e.getMessage();
		stackTrace = sw.toString();
		exceptionName = e.getClass().getName();
		this.filename = filename;
		
		pw.close();
	}
}
