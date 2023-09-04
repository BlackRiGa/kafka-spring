package ru.demo.service.soapMessageFactory;

import org.springframework.stereotype.Component;

import javax.xml.soap.*;

@Component
public class SoapMessageFactoryCreateObject {
    public SOAPMessage createSoapRequest(Integer listId, String name, String prefix, Integer code, String typeUnit, Integer codeMDM) throws Exception {
        // Создание фабрики для SOAP-сообщений
        MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = factory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Добавление пространства имен
        envelope.addNamespaceDeclaration("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("ns1", "http://tempuri.org/");
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        // Создание элемента тела запроса
        SOAPBody body = envelope.getBody();
        String createObjectLocalName = "CreateObject";
        SOAPElement createObjectElement = body.addChildElement(createObjectLocalName, "ns1");

        // Добавление элемента ListID
        String listIdElementName = "ListID";
        SOAPElement listIdElement = createObjectElement.addChildElement(listIdElementName, "ns1");
        listIdElement.setTextContent(String.valueOf(listId));

        // Создание элемента ItemFields
        String itemFieldsElementName = "ItemFields";
        SOAPElement itemFieldsElement = createObjectElement.addChildElement(itemFieldsElementName, "ns1");

        // Создание элементов ItemField с переданными значениями
        createItemField(itemFieldsElement, "Название", name);
        createItemField(itemFieldsElement, "Префикс", prefix);
        createItemField(itemFieldsElement, "Код", String.valueOf(code));
        if(typeUnit.equals("companies")){
            createItemField(itemFieldsElement, "Код МДМ", String.valueOf(codeMDM));
        }

        soapMessage.saveChanges();

        return soapMessage;
    }

    private void createItemField(SOAPElement parentElement, String fieldName, String fieldValue) throws SOAPException {
        SOAPElement itemFieldElement = parentElement.addChildElement("ItemField", "ns1");

        SOAPElement fieldNameElement = itemFieldElement.addChildElement("FieldName", "ns1");
        fieldNameElement.setTextContent(fieldName);

        SOAPElement fieldStringValueElement = itemFieldElement.addChildElement("FieldStringValue", "ns1");
        fieldStringValueElement.setTextContent(fieldValue);
    }
}