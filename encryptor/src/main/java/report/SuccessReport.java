
package report;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "successReport")
public class SuccessReport implements Report{
	
	private Status status;
	private int time;


	public SuccessReport() {
		status = Status.SUCCESS;
		time = -1;
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	
}
