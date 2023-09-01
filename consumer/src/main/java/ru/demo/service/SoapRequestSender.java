package ru.demo.service;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.demo.service.converter.SoapMessageConverter;

import javax.xml.soap.SOAPMessage;
import java.io.IOException;

@Service
public class SoapRequestSender {
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
    private String addressRequest;

    @Autowired
    public SoapRequestSender(SoapMessageConverter soapMessageConverter) {
        this.soapMessageConverter = soapMessageConverter;
    }

    public String sendSoapRequest(SOAPMessage soapMessage) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new NTCredentials(login, password, host_name, domain));

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();

        HttpPost post = new HttpPost(addressRequest); //Provide Request URL

        try {
            // Преобразуем SOAP-сообщение в массив байтов
            byte[] soapBytes = soapMessageConverter.convertSoapMessageToBytes(soapMessage);
            // Устанавливаем заголовки
            post.setHeader("Content-type", "application/soap+xml;charset=UTF-8");
            post.setHeader("SoapAction", "http://tempuri.org/FindObject"); // Укажите необходимое действие SoapAction
            // Создаем HttpEntity из массива байтов SOAP-сообщения
            HttpEntity entity = new ByteArrayEntity(soapBytes);
            // Устанавливаем HttpEntity в HttpPost
            post.setEntity(entity);

            org.apache.http.HttpResponse response = client.execute(post);

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity, "UTF-8");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Error";
    }
}
