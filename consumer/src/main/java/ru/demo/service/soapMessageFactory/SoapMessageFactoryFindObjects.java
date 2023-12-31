package ru.demo.service.soapMessageFactory;

import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.soap.*;

@Component
public class SoapMessageFactoryFindObjects {
    public SOAPMessage createSoapRequestFindObjects(Integer listId, String valueName, int valueCode) throws Exception {

        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");

        SOAPBody body = envelope.getBody();

        QName findObjectsQName = new QName("http://tempuri.org/", "FindObjects", "tem");
        SOAPElement findObjectsElement = body.addChildElement(findObjectsQName);

        QName listIdQName = new QName("http://tempuri.org/", "ListID", "tem");
        SOAPElement listIdElement = findObjectsElement.addChildElement(listIdQName);
        listIdElement.setTextContent(String.valueOf(listId));

        QName queryQName = new QName("http://tempuri.org/", "Query", "tem");
        SOAPElement queryElement = findObjectsElement.addChildElement(queryQName);
        queryElement.setTextContent("[" + valueName + "]='" + valueCode + "'");

        QName fieldNamesQName = new QName("http://tempuri.org/", "FieldNames", "tem");
        SOAPElement fieldNamesElement = findObjectsElement.addChildElement(fieldNamesQName);

        QName stringQName = new QName("http://tempuri.org/", "string", "tem");
        SOAPElement stringElement = fieldNamesElement.addChildElement(stringQName);
        stringElement.setTextContent(valueName);

        soapMessage.saveChanges();

        return soapMessage;
    }

}
