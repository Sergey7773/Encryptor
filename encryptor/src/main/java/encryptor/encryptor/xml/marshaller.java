package encryptor.encryptor.xml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import reports.FailureReport;
import reports.Reports;
import reports.SuccessReport;

public class marshaller {
	public static void main(String[] args) {
		try {
			Reports reports = new Reports();
			
			reports.getReports().add(new SuccessReport());
			reports.getReports().add(new FailureReport());
			
			JAXBContext jc = JAXBContext.newInstance("reports");

			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(reports, System.out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
	}
}
