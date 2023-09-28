package ru.demo.converter;

import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class SoapMessageConverter {
    public byte[] convertSoapMessageToBytes(SOAPMessage soapMessage) throws IOException, SOAPException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        return out.toByteArray();
    }
}
