package ru.demo.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.demo.service.converter.SoapMessageConverter;
import ru.demo.service.datahandler.IncomingDataHandler;
import ru.demo.service.datahandler.JsonParser;
import ru.demo.service.datahandler.XMLParser;
import ru.demo.service.models.CompanyModelResponseFromHr;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.util.HashMap;

@Service
@Slf4j
public class SoapRequestSender {
    private final SoapMessageConverter soapMessageConverter;
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final IncomingDataHandler dataHandler;
    private final JsonParser jsonParser;
    private final XMLParser xmlParser;
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
    public SoapRequestSender(SoapMessageConverter soapMessageConverter, SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, IncomingDataHandler dataHandler, JsonParser jsonParser, XMLParser xmlParser) {
        this.soapMessageConverter = soapMessageConverter;
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.dataHandler = dataHandler;
        this.jsonParser = jsonParser;
        this.xmlParser = xmlParser;
    }

    private String sendSoapRequest(SOAPMessage soapMessage) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new NTCredentials(login, password, host_name, domain));

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();

        HttpPost post = new HttpPost(addressRequestToWss);

        try {
            byte[] soapBytes = soapMessageConverter.convertSoapMessageToBytes(soapMessage);
            HttpEntity entity = new ByteArrayEntity(soapBytes);

            post.setHeader("Content-type", "application/soap+xml;charset=UTF-8");
            post.setHeader("SoapAction", "http://tempuri.org/FindObject");
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


    public void choiceOfMethodAction(String key, String value) throws Exception {
        String booleanAddressHr = jsonParser.returnBooleanData(value);
        Integer idFromHr = jsonParser.returnId(value);
        String responseFromHr = dataHandler.requestToHR(booleanAddressHr, idFromHr);

        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject(responseFromHr);

        switch (booleanAddressHr) {
            case "isCompany" -> {
                if (!jsonObject.get("data").toString().contains("[]")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    CompanyModelResponseFromHr companyModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), CompanyModelResponseFromHr.class);
                    SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(105, "Код", companyModel.getId());
                    String responseFromWSS = sendSoapRequest(soapMessageFindObjects);

                    if (responseFromWSS.contains("<OperationResult>true</OperationResult>")) {
                        HashMap<String, String> hashValueMapForUpdate = new HashMap<>();
                        hashValueMapForUpdate.put("Название", companyModel.getName());
                        hashValueMapForUpdate.put("Префикс", companyModel.getPrefix());
                        hashValueMapForUpdate.put("Код МДМ", String.valueOf(companyModel.getOrgCodeMdm()));
                        hashValueMapForUpdate.put("Код", String.valueOf(companyModel.getId()));
                        SOAPMessage soapMessageUpdated = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(105, xmlParser.returnItemIdFromWSS(responseFromWSS), hashValueMapForUpdate);
                        System.out.println("soapMessageUpdated" + sendSoapRequest(soapMessageUpdated));
                    } else {
                        SOAPMessage soapMessageCreateObject = soapMessageFactoryCreateObject.createSoapRequest("companies", 105, companyModel.getName(), companyModel.getPrefix(), companyModel.getId(), companyModel.getOrgCodeMdm());
                        System.out.println(soapMessageCreateObject);
                        sendSoapRequest(soapMessageCreateObject);
                    }
                } else {
                    log.info("Data from hr in id:" + jsonParser.returnId(value) + "is null");
                }
            }
            case "isStuff" -> System.out.println("isStuff");
            case "isUnit" -> System.out.println("isUnit");
            default -> {
                System.out.println(responseFromHr);
                log.info("error in switch-case in choiceOfMethodAction");
            }
        }
    }
}
