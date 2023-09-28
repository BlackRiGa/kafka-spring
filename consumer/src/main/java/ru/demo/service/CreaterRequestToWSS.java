package ru.demo.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.demo.converter.SoapMessageConverter;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;

@Component
public class CreaterRequestToWSS {
    private static final Logger log = LoggerFactory.getLogger(CreaterRequestToWSS.class);
    private final SoapMessageConverter soapMessageConverter;
    @Value("${wss.host_name}")
    private String host_name;
    @Value("${wss.domain}")
    private String domain;
    @Value("${wss.password}")
    private String password;
    @Value("${wss.login}")
    private String login;
    @Value("${wss.addressRequest}")
    private String addressRequestToWss;

    @Autowired
    public CreaterRequestToWSS(SoapMessageConverter soapMessageConverter) {
        this.soapMessageConverter = soapMessageConverter;
    }

    private CloseableHttpClient createHttpClient() {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(login, password, host_name, domain));
        return HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
    }

    String sendSoapRequest(SOAPMessage soapMessage) {
        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost post = new HttpPost(addressRequestToWss);
            byte[] soapBytes = soapMessageConverter.convertSoapMessageToBytes(soapMessage);
            HttpEntity entity = new ByteArrayEntity(soapBytes);
            post.setHeader("Content-type", "application/soap+xml;charset=UTF-8");
            post.setHeader("SoapAction", "http://tempuri.org/FindObject");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity, "UTF-8");
            }
        } catch (IOException | SOAPException ex) {
            log.error("Error in sending SOAP request", ex);
        }
        return "Error in send SoapRequest";
    }
}
