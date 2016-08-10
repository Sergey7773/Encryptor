package reports;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "reports")
public class Reports {
	
	@XmlElementWrapper(name = "reportsList")
	@XmlElement (name = "report")
	private List<Report> reportsList;
	
	public Reports() {
		reportsList = new ArrayList<Report>();
	}
	
	public List<Report> getReports() {
		return reportsList;
	}
}
