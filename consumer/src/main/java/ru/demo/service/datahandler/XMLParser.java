package ru.demo.service.datahandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

@Component
@Slf4j
public class XMLParser {
    public int returnItemIdFromWSS(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"))) {
                Document document = builder.parse(inputStream);

                NodeList itemIDList = document.getElementsByTagName("ItemID");
                if (itemIDList.getLength() > 0) {
                    Element itemIDElement = (Element) itemIDList.item(0);
                    String itemIdStr = itemIDElement.getTextContent();
                    return Integer.parseInt(itemIdStr);
                } else {
                    log.info("ItemID not found");
                    return -1;  // or throw an exception depending on the desired behavior
                }
            }
        } catch (ParserConfigurationException | UnsupportedEncodingException e) {
            log.error("Error parsing XML: " + e.getMessage());
            throw new RuntimeException("Error parsing XML", e);
        } catch (NumberFormatException e) {
            log.error("Error parsing itemId to int: " + e.getMessage());
            throw new RuntimeException("Error parsing itemId to int", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }
}
