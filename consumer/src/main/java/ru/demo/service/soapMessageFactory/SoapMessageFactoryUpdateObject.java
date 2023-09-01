package ru.demo.service.soapMessageFactory;

import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.soap.*;

@Component
public class SoapMessageFactoryUpdateObject {
    public SOAPMessage createUpdateObjectSoapRequest() throws Exception {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");

        SOAPBody body = envelope.getBody();

        // Создание элемента UpdateObject
        QName updateObjectQName = new QName("http://tempuri.org/", "UpdateObject", "tem");
        SOAPElement updateObjectElement = body.addChildElement(updateObjectQName);

        // Создание элемента ListID
        QName listIdQName = new QName("http://tempuri.org/", "ListID", "tem");
        SOAPElement listIdElement = updateObjectElement.addChildElement(listIdQName);
        listIdElement.setTextContent("3");

        // Создание элемента ItemID
        QName itemIdQName = new QName("http://tempuri.org/", "ItemID", "tem");
        SOAPElement itemIdElement = updateObjectElement.addChildElement(itemIdQName);
        itemIdElement.setTextContent("2841");

        // Создание элемента Fields
        QName fieldsQName = new QName("http://tempuri.org/", "Fields", "tem");
        SOAPElement fieldsElement = updateObjectElement.addChildElement(fieldsQName);

        // Создание элемента ItemField
        QName itemFieldQName = new QName("http://tempuri.org/", "ItemField", "tem");
        SOAPElement itemFieldElement = fieldsElement.addChildElement(itemFieldQName);

        // Создание элемента FieldName
        QName fieldNameQName = new QName("http://tempuri.org/", "FieldName", "tem");
        SOAPElement fieldNameElement = itemFieldElement.addChildElement(fieldNameQName);
        fieldNameElement.setTextContent("Название");

        // Создание элемента FieldStringValue
        QName fieldStringValueQName = new QName("http://tempuri.org/", "FieldStringValue", "tem");
        SOAPElement fieldStringValueElement = itemFieldElement.addChildElement(fieldStringValueQName);
        fieldStringValueElement.setTextContent("Отдел веб-разработки 3");

        soapMessage.saveChanges();

        return soapMessage;
    }
}
