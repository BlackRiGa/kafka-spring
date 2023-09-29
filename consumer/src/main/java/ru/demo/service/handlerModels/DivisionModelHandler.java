package ru.demo.service.handlerModels;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.demo.parsers.XMLParser;
import ru.demo.models.DivisionsModelResponseFromHr;
import ru.demo.requestSenders.CreaterRequestToWSS;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.util.HashMap;

@Service
public class DivisionModelHandler {
    private static final Logger log = LoggerFactory.getLogger(CompanyModelHandler.class);
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final CreaterRequestToWSS createrRequestToWSS;
    private final XMLParser xmlParser;

    @Autowired
    public DivisionModelHandler(SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, CreaterRequestToWSS createrRequestToWSS, XMLParser xmlParser) {
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.createrRequestToWSS = createrRequestToWSS;
        this.xmlParser = xmlParser;
    }

    public void handleDivisionsModel(JSONArray dataArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DivisionsModelResponseFromHr divisionsModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), DivisionsModelResponseFromHr.class);
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(452, "Код", divisionsModel.getId());
            String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
            log.info("responseFromWSS: " + responseFromWSS);

            SOAPMessage soapMessageToSend;
            if (responseFromWSS.contains("<Fields>")) {
                soapMessageToSend = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(452, xmlParser.returnItemIdFromWSS(responseFromWSS), handleModel(divisionsModel));
            } else {
                soapMessageToSend = soapMessageFactoryCreateObject.createSoapRequest(452, handleModel(divisionsModel));
            }

            log.info("responseFromWSSCreateOrUpdate: " + createrRequestToWSS.sendSoapRequest(soapMessageToSend));
        } catch (Exception e) {
            log.error("Error while handling DivisionsModel: " + e.getMessage(), e);
        }
    }

    private HashMap<String, String> handleModel(DivisionsModelResponseFromHr divisionsModel) {
        HashMap<String, String> hashValueMapForDivision = new HashMap<>();
        hashValueMapForDivision.put("Название", divisionsModel.getName());
        hashValueMapForDivision.put("Префикс", divisionsModel.getPrefix());
        hashValueMapForDivision.put("Код", String.valueOf(divisionsModel.getId()));
        hashValueMapForDivision.put("Не актуален", String.valueOf(booleanActualFromHR(divisionsModel.getIsNotActive())));

        return hashValueMapForDivision;
    }

    private Boolean booleanActualFromHR(String isNotActive) {
        return !isNotActive.contains("0");
    }
}
