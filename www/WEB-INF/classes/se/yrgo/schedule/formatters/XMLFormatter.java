package se.yrgo.schedule.formatters;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.yrgo.schedule.domain.*;

import java.io.*;
import java.util.List;

public class XMLFormatter implements Formatter {

  @Override
  public String format(List<Assignment> assignments) {

    if (assignments.size() == 0) {
      return new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
          .append("<schedules></schedules>\n")
          .toString();
    } else {
      try {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("schedules");
        doc.appendChild(rootElement);

        for (Assignment assignment : assignments) {

          Element schedule = doc.createElement("schedule");
          schedule.setAttribute("date", assignment.date());

          Element school = doc.createElement("school");
          Element schoolName = doc.createElement("school_name");
          schoolName.appendChild(doc.createTextNode(assignment.school().name()));

          Element schoolAddress = doc.createElement("address");
          schoolAddress.appendChild(doc.createTextNode(assignment.school().address()));

          school.appendChild(schoolName);
          school.appendChild(schoolAddress);
          schedule.appendChild(school);

          Element substitute = doc.createElement("substitute");
          Element name = doc.createElement("name");
          name.appendChild(doc.createElement(assignment.teacher().name()));

          substitute.appendChild(name);
          schedule.appendChild(substitute);
          rootElement.appendChild(schedule);

        }

        StringWriter xml = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer
            .setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xml);
        transformer.transform(source, result);
        return xml.toString();

      } catch (ParserConfigurationException | TransformerException e) {
        return "XML Error";
      }

    }

  }
}