
package reports;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "successReport")
public class SuccessReport extends Report{
	
	private Status status;
	private int time;


	public SuccessReport() {
		status = Status.SUCCESS;
		time = -1;
	}
	
	@XmlElement
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status=status;
	}
	
	@XmlElement
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	
}
