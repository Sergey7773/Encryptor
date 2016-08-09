
package reports;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@XmlType(name = "successReport")
public class SuccessReport extends Report{
	
	@Getter @Setter private Status status;
	@Getter @Setter private int time;
	@Getter @Setter private String filename;

	public SuccessReport() {
		status = Status.SUCCESS;
		time = -1;
		filename = null;
	}
	
	public SuccessReport(String filename, int elapsedTime) {
		this.filename = filename;
		this.time = elapsedTime;
		status = Status.SUCCESS;
	}
}
