package ru.demo.service.soapMessageFactory;

import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SoapMessageFactoryUpdateObject {
    public SOAPMessage createUpdateObjectSoapRequest(Integer listId, Integer ItemId, HashMap<String, String> itemValueMap) throws Exception {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");

        SOAPBody body = envelope.getBody();

        QName updateObjectQName = new QName("http://tempuri.org/", "UpdateObject", "tem");
        SOAPElement updateObjectElement = body.addChildElement(updateObjectQName);

        QName listIdQName = new QName("http://tempuri.org/", "ListID", "tem");
        SOAPElement listIdElement = updateObjectElement.addChildElement(listIdQName);
        listIdElement.setTextContent(String.valueOf(listId));

        QName itemIdQName = new QName("http://tempuri.org/", "ItemID", "tem");
        SOAPElement itemIdElement = updateObjectElement.addChildElement(itemIdQName);
        itemIdElement.setTextContent(String.valueOf(ItemId));

        QName fieldsQName = new QName("http://tempuri.org/", "Fields", "tem");
        SOAPElement fieldsElement = updateObjectElement.addChildElement(fieldsQName);

        QName itemFieldQName = new QName("http://tempuri.org/", "ItemField", "tem");
        SOAPElement itemFieldElement = fieldsElement.addChildElement(itemFieldQName);
        for (Map.Entry<String, String> entry : itemValueMap.entrySet()) {
            QName fieldNameQName = new QName("http://tempuri.org/", "FieldName", "tem");
            SOAPElement fieldNameElement = itemFieldElement.addChildElement(fieldNameQName);
            fieldNameElement.setTextContent(entry.getKey());

            QName fieldStringValueQName = new QName("http://tempuri.org/", "FieldStringValue", "tem");
            SOAPElement fieldStringValueElement = itemFieldElement.addChildElement(fieldStringValueQName);
            fieldStringValueElement.setTextContent(entry.getValue());
        }
        soapMessage.saveChanges();

        return soapMessage;
    }
}
