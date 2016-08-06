package report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name = "reports")
public class Reports {
	private List<Report> reports;
	
	public Reports() {
		reports = new ArrayList<Report>();
	}
}
