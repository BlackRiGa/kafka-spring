package ru.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.demo.model.StringValue;
import ru.demo.service.converter.SoapMessageConverter;
import ru.demo.service.datahandler.IncomingDataHandler;
import ru.demo.service.models.CompaniesRequestModel;
import ru.demo.service.models.DirectionsRequestModel;
import ru.demo.service.models.DivisionsRequestModel;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.util.HashMap;

@Service
public class SoapRequestSender {
    private final SoapMessageConverter soapMessageConverter;
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final IncomingDataHandler dataHandler;

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
    public SoapRequestSender(SoapMessageConverter soapMessageConverter, SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, IncomingDataHandler dataHandler) throws Exception {
        this.soapMessageConverter = soapMessageConverter;
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.dataHandler = dataHandler;
    }

    private String sendSoapRequest(SOAPMessage soapMessage) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new NTCredentials(login, password, host_name, domain));

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();

        HttpPost post = new HttpPost(addressRequest);

        try {
            // Преобразуем SOAP-сообщение в массив байтов
            byte[] soapBytes = soapMessageConverter.convertSoapMessageToBytes(soapMessage);
            HttpEntity entity = new ByteArrayEntity(soapBytes);

            post.setHeader("Content-type", "application/soap+xml;charset=UTF-8");
            post.setHeader("SoapAction", "http://tempuri.org/FindObject"); // действие SoapAction
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
        return "Error in send SoapRequest";
    }


    public void choiceOfMethodAction(StringValue valueStr) throws Exception {
        String url;
        String sample = valueStr.value();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(valueStr.value());
            url = jsonNode.get("url").asText();
            System.out.println("URL: " + url);
        } catch (Exception e) {
            url = "error";
            e.printStackTrace();
        }

        switch (url) {
            case "directions":
                System.out.println("choiceOfMethodAction = directions");

                DirectionsRequestModel directions = DirectionsRequestModel.fromJson(sample);

                System.out.println(directions.toString());
                SOAPMessage soapMessageFound = soapMessageFactoryFindObjects.createSoapRequestFindObjects(453, "Код", directions.getData().get(0).getId());
                System.out.println("soapMessageFound = " + sendSoapRequest(soapMessageFound));

                if (soapMessageFound.getSOAPBody().getTextContent().contains("Код")) {
                    Integer itemId = 2841;
                    HashMap<String, String> hashValueMapForUpdate = new HashMap<>();
                    hashValueMapForUpdate.put("Название", directions.getData().get(0).getName());
                    SOAPMessage soapMessageUpdated = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(453, itemId, hashValueMapForUpdate);
                    System.out.println("soapMessageUpdated" + sendSoapRequest(soapMessageUpdated));
                } else {
                    SOAPMessage soapMessageCreated = soapMessageFactoryCreateObject.createSoapRequest(453, directions.getData().get(0).getName(), directions.getData().get(0).getPrefix(), directions.getData().get(0).getId(), null, null);
                    System.out.println("soapMessageCreated" + sendSoapRequest(soapMessageCreated));
                }
                break;
            case "divisions":
                System.out.println("choiceOfMethodAction = divisions");

                DivisionsRequestModel divisions = DivisionsRequestModel.fromJson(sample);
                System.out.println(divisions.getData());

                SOAPMessage soapMessage = soapMessageFactoryCreateObject.createSoapRequest(452, divisions.getData().get(0).getName(), divisions.getData().get(1).getPrefix(), divisions.getData().get(1).getId(), null, null);
                System.out.println(sendSoapRequest(soapMessage));

                break;
            case "companies":
                System.out.println("choiceOfMethodAction = companies");

                CompaniesRequestModel companies = CompaniesRequestModel.fromJson(sample);
                System.out.println(companies.getData());

                SOAPMessage soapMessageCreateObject = soapMessageFactoryCreateObject.createSoapRequest(105, companies.getData().get(0).getName(), companies.getData().get(0).getPrefix(), companies.getData().get(0).getId(), companies.getUrl(), companies.getData().get(0).getOrgCodeMdm());
                System.out.println(sendSoapRequest(soapMessageCreateObject));
                break;
            default:
                System.out.println("error in switch-case");

        }
    }
}
