package encryptor.encryptor.xml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import reports.Reports;
import encryptor.encryptor.algorithms.EncryptionAlgorithm;

public class XmlParser {
	
	public void marshall(Object obj,String outputFilepath, String schema, String contextPackage) {
		try {
			JAXBContext jc = JAXBContext.newInstance(contextPackage);

			File out = new File(outputFilepath);

			Marshaller marshaller = jc.createMarshaller();
			marshaller.setSchema(SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(new File(schema)));
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(obj, out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} 
	}

	public Object unmarshall(String filepath,String schema, String contextPackage) {
		Object result = null;
		try {
		JAXBContext jc = JAXBContext.newInstance(contextPackage);
		Unmarshaller um = jc.createUnmarshaller();
		um.setSchema(SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI )
				.newSchema(new File(schema)));
		result = um.unmarshal(new File(filepath));;
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public EncryptionAlgorithm unmarshallEncryptionAlgorithm(String filepath) {
		String schema = Utils.class.getClassLoader().getResource("EncryptionAlgorithms.xsd").getPath();
		return (EncryptionAlgorithm)(
				unmarshall(filepath,schema,"encryptor.encryptor.algorithms"));
	}
	
	public void marshallEncryptionAlgorithm(EncryptionAlgorithm alg, String filepath) {
		String schema = Utils.class.getClassLoader().getResource("EncryptionAlgorithms.xsd").getPath();
		marshall(alg,filepath,schema,"encryptor.encryptor.algorithms");
	}
	
	public void marshallReports(Reports reports, String filepath) {
		String schema = Utils.class.getClassLoader().getResource("Reports.xsd").getPath();
		marshall(reports,filepath,schema,"reports");
	}
	
}
