package com.serdtsev;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * User: Andrey Serdtsev
 */
public class XmlUtils {
  /**
   * Convert String to DOM Document object
   */
  public static org.w3c.dom.Document getDomDocument(String xmlString)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return builder.parse(new InputSource(new StringReader(xmlString)));
  }

  /**
   * Convert File to DOM Document object
   */
  public static org.w3c.dom.Document getDomDocument(File xmlFile)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    InputStream inputStream = new FileInputStream(xmlFile);
    InputSource inputSource = new InputSource(new InputStreamReader(inputStream));
    return builder.parse(inputSource);
  }
}
