package ru.demo.service.soapMessageFactory;

import org.springframework.stereotype.Component;

import javax.xml.soap.*;

@Component
public class SoapMessageFactoryFindObject {
    public SOAPMessage createSoapRequestFindObject() throws Exception {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");

        SOAPBody body = envelope.getBody();

        // Создание элементов тела запроса, включая необходимые данные
        String findObjectLocalName = "FindObject";
        SOAPElement findObjectElement = body.addChildElement(findObjectLocalName, "tem", "http://tempuri.org/");

        String listIdElementName = "ListID";
        SOAPElement listIdElement = findObjectElement.addChildElement(listIdElementName, "tem", "http://tempuri.org/");
        listIdElement.setTextContent("3");

        String itemIdElementName = "ItemID";
        SOAPElement itemIdElement = findObjectElement.addChildElement(itemIdElementName, "tem", "http://tempuri.org/");
        itemIdElement.setTextContent("2841");

        String fieldNamesQName = "FieldNames";
        SOAPElement fieldNamesElement = findObjectElement.addChildElement(fieldNamesQName, "tem", "http://tempuri.org/");

        String stringQName = "string";
        String originalText = "Название"; // Ваш текст в кодировке UTF-8
        SOAPElement stringElement = fieldNamesElement.addChildElement(stringQName, "tem", "http://tempuri.org/");
        stringElement.setTextContent(originalText);

        soapMessage.saveChanges();

        return soapMessage;
    }
}
