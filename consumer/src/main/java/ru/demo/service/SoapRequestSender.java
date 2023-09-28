package ru.demo.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.demo.datahandler.IncomingDataHandler;
import ru.demo.datahandler.JsonParser;
import ru.demo.datahandler.XMLParser;
import ru.demo.models.CompanyModelResponseFromHr;
import ru.demo.models.DivisionsModelResponseFromHr;
import ru.demo.service.models.DirectionsModelResponseFromHr;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.util.HashMap;

@Service
public class SoapRequestSender {
    private static final Logger log = LoggerFactory.getLogger(SoapRequestSender.class);
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final IncomingDataHandler dataHandler;
    private final JsonParser jsonParser;
    private final XMLParser xmlParser;
    private final CreaterRequestToWSS createrRequestToWSS;


    @Autowired
    public SoapRequestSender(SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, IncomingDataHandler dataHandler, JsonParser jsonParser, XMLParser xmlParser, CreaterRequestToWSS createrRequestToWSS) {
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.dataHandler = dataHandler;
        this.jsonParser = jsonParser;
        this.xmlParser = xmlParser;
        this.createrRequestToWSS = createrRequestToWSS;
    }

    public void choiceOfMethodAction(String key, String value) {
        try {
            String booleanAddressHr = jsonParser.returnBooleanData(value);
            Integer idFromHr = jsonParser.returnId(value);
            String responseFromHr = dataHandler.requestToHR(booleanAddressHr, idFromHr);

            JSONObject jsonObject = new JSONObject(responseFromHr);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            if (!dataArray.isEmpty()) {
                switch (booleanAddressHr) {
                    case "isCompany" -> handleCompanyModel(dataArray);
                    case "isDirection" -> handleDirectionsModel(dataArray);
                    case "isDivision" -> handleDivisionsModel(dataArray);
                    case "isStuff" -> System.out.println("isStuff");
                    case "isUnit" -> System.out.println("isUnit");
                    default -> log.info("Ошибка в операторе switch-case в методе choiceOfMethodAction");
                }
            } else {
                log.info("Данные от HR по идентификатору " + jsonParser.returnId(value) + " отсутствуют");
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса: " + e.getMessage(), e);
        }
    }

    private void handleCompanyModel(JSONArray dataArray) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CompanyModelResponseFromHr companyModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), CompanyModelResponseFromHr.class);
        SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(105, "Код", companyModel.getId());
        String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
        log.info("responseFromWSS: " + responseFromWSS);
        if (responseFromWSS.contains("<Fields>")) {
            SOAPMessage soapMessageUpdated = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(105, xmlParser.returnItemIdFromWSS(responseFromWSS), handleCompanyModel(companyModel));
            log.info("responseFromWSSUpdateObject: " + createrRequestToWSS.sendSoapRequest(soapMessageUpdated));
        } else {
            SOAPMessage soapMessageCreateObject = soapMessageFactoryCreateObject.createSoapRequest(105, handleCompanyModel(companyModel));
            log.info("responseFromWSSCreateObject: " + createrRequestToWSS.sendSoapRequest(soapMessageCreateObject));
        }
    }

    private void handleDirectionsModel(JSONArray dataArray) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        DirectionsModelResponseFromHr directionsModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), DirectionsModelResponseFromHr.class);
        SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(453, "Код", directionsModel.getId());
        String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
        log.info("responseFromWSS: " + responseFromWSS);
        if (responseFromWSS.contains("<Fields>")) {
            SOAPMessage soapMessageUpdated = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(453, xmlParser.returnItemIdFromWSS(responseFromWSS), handleDirectionsModel(directionsModel));
            log.info("responseFromWSSUpdateObject: " + createrRequestToWSS.sendSoapRequest(soapMessageUpdated));
        } else {
            SOAPMessage soapMessageCreateObject = soapMessageFactoryCreateObject.createSoapRequest(453, handleDirectionsModel(directionsModel));
            log.info("responseFromWSSCreateObject: " + createrRequestToWSS.sendSoapRequest(soapMessageCreateObject));
        }
    }

    private void handleDivisionsModel(JSONArray dataArray) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        DivisionsModelResponseFromHr divisionsModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), DivisionsModelResponseFromHr.class);
        SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(452, "Код", divisionsModel.getId());
        String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
        log.info("responseFromWSS: " + responseFromWSS);
        if (responseFromWSS.contains("<Fields>")) {
            SOAPMessage soapMessageUpdated = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(452, xmlParser.returnItemIdFromWSS(responseFromWSS), handleDivisionsModel(divisionsModel));
            log.info("responseFromWSSUpdateObject: " + createrRequestToWSS.sendSoapRequest(soapMessageUpdated));
        } else {
            SOAPMessage soapMessageCreateObject = soapMessageFactoryCreateObject.createSoapRequest(452, handleDivisionsModel(divisionsModel));
            log.info("responseFromWSSCreateObject: " + createrRequestToWSS.sendSoapRequest(soapMessageCreateObject));
        }
    }

    private HashMap<String, String> handleCompanyModel(CompanyModelResponseFromHr companyModel) {
        HashMap<String, String> hashValueMapForCompany = new HashMap<>();
        hashValueMapForCompany.put("Название", companyModel.getName());
        hashValueMapForCompany.put("Префикс", companyModel.getPrefix());
        hashValueMapForCompany.put("Код МДМ", String.valueOf(companyModel.getOrgCodeMdm()));
        hashValueMapForCompany.put("Код", String.valueOf(companyModel.getId()));
        return hashValueMapForCompany;
    }

    private HashMap<String, String> handleDirectionsModel(DirectionsModelResponseFromHr directionsModel) throws Exception {
        System.out.println(directionsModel);
        HashMap<String, String> hashValueMapForDirection = new HashMap<>();
        hashValueMapForDirection.put("Название", directionsModel.getName());
        hashValueMapForDirection.put("Префикс", directionsModel.getPrefix());



        String test = dataHandler.requestToHR("isDivision", directionsModel.getParent_id());

        JSONObject jsonObject = new JSONObject(test);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        ObjectMapper objectMapper = new ObjectMapper();
        DivisionsModelResponseFromHr divisionsModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), DivisionsModelResponseFromHr.class);
        SOAPMessage soapMessageFindObjects = null;
        try {
            soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(452, "Код", divisionsModel.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);



        hashValueMapForDirection.put("Дивизион", String.valueOf(xmlParser.returnItemIdFromWSS(responseFromWSS)));//TODO ИЗМЕНИТЬ НА ID ДИВИЗИОНА В WSS
        hashValueMapForDirection.put("Код", String.valueOf(directionsModel.getId()));
        hashValueMapForDirection.put("Не актуален", String.valueOf(booleanActualFromHR(directionsModel.getIsNotActive())));
        return hashValueMapForDirection;
    }

    private Boolean booleanActualFromHR(String isNotActive) {
        return !isNotActive.contains("0");
    }

    private HashMap<String, String> handleDivisionsModel(DivisionsModelResponseFromHr divisionsModel) {
        HashMap<String, String> hashValueMapForDivision = new HashMap<>();
        hashValueMapForDivision.put("Название", divisionsModel.getName());
        hashValueMapForDivision.put("Префикс", divisionsModel.getPrefix());
        hashValueMapForDivision.put("Код", String.valueOf(divisionsModel.getId()));
        hashValueMapForDivision.put("Не актуален", String.valueOf(booleanActualFromHR(divisionsModel.getIsNotActive())));

        return hashValueMapForDivision;
    }
}
