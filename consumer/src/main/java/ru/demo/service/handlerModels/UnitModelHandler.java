package ru.demo.service.handlerModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.demo.requestSenders.IncomingDataHandler;
import ru.demo.parsers.XMLParser;
import ru.demo.models.CompanyModelResponseFromHr;
import ru.demo.models.UnitModelResponseFromHr;
import ru.demo.requestSenders.CreaterRequestToWSS;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.util.HashMap;

@Service
public class UnitModelHandler {
    private static final Logger log = LoggerFactory.getLogger(UnitModelHandler.class);
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final CreaterRequestToWSS createrRequestToWSS;
    private final XMLParser xmlParser;
    private final IncomingDataHandler dataHandler;

    @Autowired
    public UnitModelHandler(SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, CreaterRequestToWSS createrRequestToWSS, XMLParser xmlParser, IncomingDataHandler dataHandler) {
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.createrRequestToWSS = createrRequestToWSS;
        this.xmlParser = xmlParser;
        this.dataHandler = dataHandler;
    }

    public void handleUnitModel(JSONArray dataArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UnitModelResponseFromHr unitModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), UnitModelResponseFromHr.class);
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(3, "Код", unitModel.getId());
            String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
            log.info("responseFromWSS: " + responseFromWSS);

            SOAPMessage soapMessageToSend;
            if (responseFromWSS.contains("<Fields>")) {
                soapMessageToSend = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(3, xmlParser.returnItemIdFromWSS(responseFromWSS), handleUnitModel(unitModel));
            } else {
                soapMessageToSend = soapMessageFactoryCreateObject.createSoapRequest(3, handleUnitModel(unitModel));
            }

            log.info("responseFromWSSCreateOrUpdate: " + createrRequestToWSS.sendSoapRequest(soapMessageToSend));
        } catch (Exception e) {
            log.error("Error while handling UnitModel: " + e.getMessage(), e);
        }
    }

    private HashMap<String, String> handleUnitModel(UnitModelResponseFromHr unitModel) throws JsonProcessingException {
        HashMap<String, String> hashValueMapForUnit = new HashMap<>();
        hashValueMapForUnit.put("Название", unitModel.getName());
        hashValueMapForUnit.put("Компания", searchNameCompanyInWSS(unitModel));
        hashValueMapForUnit.put("Код", String.valueOf(unitModel.getId()));
        hashValueMapForUnit.put("Родительское подразделение", searchNameParentUnitInWSS(unitModel));
        hashValueMapForUnit.put("Не актуален", String.valueOf(booleanActualFromHR(unitModel.getIsNotActive())));
        return hashValueMapForUnit;
    }

    private String searchNameCompanyInWSS(UnitModelResponseFromHr unitModel) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject(dataHandler.requestToHR("isCompany", unitModel.getCompany_id()));
        JSONArray dataArray = jsonObject.getJSONArray("data");
        CompanyModelResponseFromHr companyModel = new ObjectMapper().readValue(dataArray.getJSONObject(0).toString(), CompanyModelResponseFromHr.class);
        try {
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(105, "Код", companyModel.getId());
            return String.valueOf(xmlParser.returnItemIdFromWSS(createrRequestToWSS.sendSoapRequest(soapMessageFindObjects)));
        } catch (Exception e) {
            log.info("Error in searchNameCompanyInWSS in request to WSS for UnitId");
            return "Error in searchNameCompanyInWSS in request to WSS for UnitId";
        }
    }

    private String searchNameParentUnitInWSS(UnitModelResponseFromHr unitModel) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject(dataHandler.requestToHR("isUnit", unitModel.getParent_id()));
        JSONArray dataArray = jsonObject.getJSONArray("data");
        UnitModelResponseFromHr unitModelFromHr = new ObjectMapper().readValue(dataArray.getJSONObject(0).toString(), UnitModelResponseFromHr.class);
        try {
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(3, "Код", unitModelFromHr.getId());
            return String.valueOf(xmlParser.returnItemIdFromWSS(createrRequestToWSS.sendSoapRequest(soapMessageFindObjects)));
        } catch (Exception e) {
            log.info("Error in searchNameParentUnitInWSS in request to WSS for UnitId");
            return "Error in searchNameParentUnitInWSS in request to WSS for UnitId";
        }
    }

    private Boolean booleanActualFromHR(String isNotActive) {
        return !isNotActive.contains("0");
    }
}
