package eand.com.bch.batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlItemProcessor implements ItemProcessor<String,String>  {

	@Value("${skip_doctypes}")
	private String[] skip_doctypes;

	@Value("${masked_text}")
	private String masked_text;

	@Value("${xml_output_path}")
	private String xml_output_path;


	@Override
	public String process(String item) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File(item));
		doc.getDocumentElement().normalize();
		String allDocType="";
		NodeList Header = doc.getElementsByTagName("Header");
		Node node_header = Header.item(0);
		Attr attr_invoice_number = (Attr) node_header.getAttributes().getNamedItem("RefNum");		
		String Invoice_Number = attr_invoice_number.getValue();		
		NodeList BillAcc = doc.getElementsByTagName("Part");


		for (int i1 = 0; i1 < BillAcc.getLength(); i1++) {
			Node node1 = BillAcc.item(i1);
			Attr attr_DocType = (Attr) node1.getAttributes().getNamedItem("DocType");
			String attr_DocType_String = attr_DocType.getValue();
			boolean isskipped=false;

			for (String s_doctypes: skip_doctypes) {
				if (s_doctypes.equalsIgnoreCase(attr_DocType_String)) {
					isskipped=true;
					break;
				}
			}

			if (!isskipped) {
				Attr attr_File = (Attr) node1.getAttributes().getNamedItem("File");
				allDocType = allDocType+attr_DocType_String+":"+attr_File+"\n";
				if (attr_DocType_String.equalsIgnoreCase("ADD")) {
					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_ADD = dBuilder.parse(new File(filename));
					NodeList InvPartynodes = doc_ADD.getElementsByTagName("InvParty");
					Node InvPartynode = InvPartynodes.item(0);
					Attr attr_VATRegNo = (Attr) InvPartynode.getAttributes().getNamedItem("VATRegNo");
					String VATRegNo=attr_VATRegNo.getTextContent();
					if (VATRegNo.length() !=0)
					{
						attr_VATRegNo.setNodeValue(masked_text);
					}

					NodeList Addrnodes = doc_ADD.getElementsByTagName("Addr");
					for(int i=0; i<Addrnodes.getLength(); i++) {
						Node Addrnode = Addrnodes.item(i);
						Attr attr_Name = (Attr) Addrnode.getAttributes().getNamedItem("Name");
						attr_Name.setNodeValue(masked_text);

						Attr attr_City = (Attr) Addrnode.getAttributes().getNamedItem("City");
						attr_City.setNodeValue(masked_text);

						Attr attr_Country = (Attr) Addrnode.getAttributes().getNamedItem("Country");
						attr_Country.setNodeValue(masked_text);

						Attr attr_Line1 = (Attr) Addrnode.getAttributes().getNamedItem("Line1");
						attr_Line1.setNodeValue(masked_text);

						Attr attr_Line2 = (Attr) Addrnode.getAttributes().getNamedItem("Line2");
						attr_Line2.setNodeValue(masked_text);

						Attr attr_Line3 = (Attr) Addrnode.getAttributes().getNamedItem("Line3");
						attr_Line3.setNodeValue(masked_text);

						Attr attr_Zip = (Attr) Addrnode.getAttributes().getNamedItem("Zip");
						attr_Zip.setNodeValue(masked_text);

						if (i!=0) {
							Attr attr_Email = (Attr) Addrnode.getAttributes().getNamedItem("Email");
							attr_Email.setNodeValue(masked_text);

							Attr attr_FName = (Attr) Addrnode.getAttributes().getNamedItem("FName");
							attr_FName.setNodeValue(masked_text);

							Attr attr_Addr1 = (Attr) Addrnode.getAttributes().getNamedItem("Addr1");
							attr_Addr1.setNodeValue(masked_text);

							Attr attr_Addr2 = (Attr) Addrnode.getAttributes().getNamedItem("Addr2");
							attr_Addr2.setNodeValue(masked_text);

							Attr attr_Addr3 = (Attr) Addrnode.getAttributes().getNamedItem("Addr3");
							attr_Addr3.setNodeValue(masked_text);

							Attr attr_LName = (Attr) Addrnode.getAttributes().getNamedItem("LName");
							attr_LName.setNodeValue(masked_text);

							Attr attr_Line4 = (Attr) Addrnode.getAttributes().getNamedItem("Line4");
							attr_Line4.setNodeValue(masked_text);

							Attr attr_Line5 = (Attr) Addrnode.getAttributes().getNamedItem("Line5");
							attr_Line5.setNodeValue(masked_text);

							Attr attr_Line6 = (Attr) Addrnode.getAttributes().getNamedItem("Line6");
							attr_Line6.setNodeValue(masked_text);

							Attr attr_MSISDN = (Attr) Addrnode.getAttributes().getNamedItem("MSISDN");
							attr_MSISDN.setNodeValue(masked_text);

						}       				

					}

					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_ADD);
				} else if (attr_DocType_String.equalsIgnoreCase("SUM")) {
					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_SUM = dBuilder.parse(new File(filename));
					NodeList Addrnodes = doc_SUM.getElementsByTagName("Addr");
					for(int i=0; i<Addrnodes.getLength(); i++) {
						Node Addrnode = Addrnodes.item(i);
						Attr attr_Name = (Attr) Addrnode.getAttributes().getNamedItem("Name");
						attr_Name.setNodeValue(masked_text);

						Attr attr_City = (Attr) Addrnode.getAttributes().getNamedItem("City");
						attr_City.setNodeValue(masked_text);

						Attr attr_Country = (Attr) Addrnode.getAttributes().getNamedItem("Country");
						attr_Country.setNodeValue(masked_text);

						Attr attr_Line1 = (Attr) Addrnode.getAttributes().getNamedItem("Line1");
						attr_Line1.setNodeValue(masked_text);

						Attr attr_Line2 = (Attr) Addrnode.getAttributes().getNamedItem("Line2");
						attr_Line2.setNodeValue(masked_text);

						Attr attr_Line3 = (Attr) Addrnode.getAttributes().getNamedItem("Line3");
						attr_Line3.setNodeValue(masked_text);

						Attr attr_Line4 = (Attr) Addrnode.getAttributes().getNamedItem("Line4");
						attr_Line4.setNodeValue(masked_text);

						Attr attr_Line5 = (Attr) Addrnode.getAttributes().getNamedItem("Line5");
						attr_Line5.setNodeValue(masked_text);

						Attr attr_Line6 = (Attr) Addrnode.getAttributes().getNamedItem("Line6");
						attr_Line6.setNodeValue(masked_text);

						Attr attr_Zip = (Attr) Addrnode.getAttributes().getNamedItem("Zip");
						attr_Zip.setNodeValue(masked_text);

						Attr attr_Addr1 = (Attr) Addrnode.getAttributes().getNamedItem("Addr1");
						attr_Addr1.setNodeValue(masked_text);

						Attr attr_Addr2 = (Attr) Addrnode.getAttributes().getNamedItem("Addr2");
						attr_Addr2.setNodeValue(masked_text);

						Attr attr_Addr3 = (Attr) Addrnode.getAttributes().getNamedItem("Addr3");
						attr_Addr3.setNodeValue(masked_text);

						Attr attr_Email = (Attr) Addrnode.getAttributes().getNamedItem("Email");
						attr_Email.setNodeValue(masked_text);

						Attr attr_FName = (Attr) Addrnode.getAttributes().getNamedItem("FName");
						attr_FName.setNodeValue(masked_text);

						Attr attr_LName = (Attr) Addrnode.getAttributes().getNamedItem("LName");
						attr_LName.setNodeValue(masked_text);

						Attr attr_MSISDN = (Attr) Addrnode.getAttributes().getNamedItem("MSISDN");
						attr_MSISDN.setNodeValue(masked_text);
					}
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_SUM);
				} else if (attr_DocType_String.equalsIgnoreCase("AGG")) {

					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_AGG = dBuilder.parse(new File(filename));
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_AGG);
				} else if (attr_DocType_String.equalsIgnoreCase("BAL")) {

					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_BAL = dBuilder.parse(new File(filename));
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_BAL);        				
				} else if (attr_DocType_String.equalsIgnoreCase("CIN")) {

					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_CIN = dBuilder.parse(new File(filename));
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_CIN);        				
				} else if (attr_DocType_String.equalsIgnoreCase("IIN")) {

					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_IIN = dBuilder.parse(new File(filename));
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_IIN);        				
				} else if (attr_DocType_String.equalsIgnoreCase("INV")) {

					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_INV = dBuilder.parse(new File(filename));
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_INV);        				
				} else if (attr_DocType_String.equalsIgnoreCase("XCD")) {

					String filename= (attr_File.getValue().replaceAll("file:", "")).replaceAll("\"", "");
					Document doc_XCD = dBuilder.parse(new File(filename));
					savemodifiedfile(xml_output_path+Invoice_Number+"/", filename,doc_XCD);        				
				} else{

				}
			}
			else {}
		}
		return Invoice_Number;
	}


	public void savemodifiedfile(String folder_path, String filename, Document doc) throws TransformerException, IOException {
		Path p = Paths.get(filename);
		filename=p.getFileName().toString();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(doc);
		File file = new File(folder_path+filename);
		file.getParentFile().mkdir();
		file.createNewFile();
		StreamResult streamResult = new StreamResult(file);
		transformer.transform(domSource, streamResult);
		Logger LOGs = LoggerFactory.getLogger(XmlItemProcessor.class);
		LOGs.info("Xml file copied: "+filename);
	}



}