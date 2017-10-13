package org.tchm.readFiles;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLReader {

    public List<String[]> readXML(File file){
        List<String[]> out = new ArrayList<>();

        try {
            File inputFile = file;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("data_publikacji");
            String date2 = nList.item(0).getTextContent();
            NodeList nList2 = doc.getElementsByTagName("pozycja");

            for(int i = 0; i < nList2.getLength();i++){
                String[] line = new String[5];
                Node nNode = nList2.item(i);
                Element element = (Element) nNode;
                line[0] = element.getElementsByTagName("kod_waluty").item(0).getTextContent();
                line[1] = element.getElementsByTagName("kurs_sredni").item(0).getTextContent();
                line[2] = element.getElementsByTagName("przelicznik").item(0).getTextContent();
                line[3] = element.getElementsByTagName("nazwa_waluty").item(0).getTextContent();
                line[4] = date2;
                out.add(line);
            }

        } catch(Exception e) {

        }
        return out;
    }

}
