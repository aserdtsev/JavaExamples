package com.serdtsev;

import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/**
 * User: Andrey Serdtsev
 */
public class XmlTest {

  @Test
  public void testGetDomDocumentByString() throws Exception {
    String xmlString = "<note author=\"Andrey\">\n" +
        "    <to>Tove</to>\n" +
        "    <from>Jani</from>\n" +
        "    <heading>Reminder</heading>\n" +
        "    <body>Don't forget me this weekend!</body>\n" +
        "</note>";
    assertNotNull(XmlUtils.getDomDocument(xmlString));
  }

  @Test
  public void testGetDomDocumentByFile() throws Exception {
    File xmlFile = new File("src/test/xml/note.xml");
    assertNotNull(XmlUtils.getDomDocument(xmlFile));
  }

  @Test
  public void testXPath() throws Exception {
    File xmlFile = new File("src/test/xml/note.xml");
    Document document = XmlUtils.getDomDocument(xmlFile);
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();

    assertEquals("Tove", xpath.compile("/note/to").evaluate(document));
    assertEquals("Andrey", xpath.compile("/note/@author").evaluate(document));
  }

}
