package ru.demo.service.soapMessageFactory;

import org.springframework.stereotype.Component;

import javax.xml.soap.*;

@Component
public class SoapMessageFactoryCreateObject {
    public SOAPMessage createSoapRequest(String typeUnit, Integer listId, String name, String prefix, Integer code, Integer codeMDM) throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns1", "http://tempuri.org/");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        SOAPBody body = envelope.getBody();
        SOAPElement createObjectElement = body.addChildElement("CreateObject", "ns1");

        createObjectElement.addChildElement("ListID", "ns1").setTextContent(String.valueOf(listId));

        SOAPElement itemFieldsElement = createObjectElement.addChildElement("ItemFields", "ns1");

        createItemField(itemFieldsElement, "Название", name);
        createItemField(itemFieldsElement, "Префикс", prefix);
        createItemField(itemFieldsElement, "Код", String.valueOf(code));

        if (typeUnit.equals("companies")) {
            createItemField(itemFieldsElement, "Код МДМ", String.valueOf(codeMDM));
        }

        soapMessage.saveChanges();

        return soapMessage;
    }

    private void createItemField(SOAPElement parentElement, String fieldName, String fieldValue) throws SOAPException {
        SOAPElement itemFieldElement = parentElement.addChildElement("ItemField", "ns1");
        itemFieldElement.addChildElement("FieldName", "ns1").setTextContent(fieldName);
        itemFieldElement.addChildElement("FieldStringValue", "ns1").setTextContent(fieldValue);
    }
}