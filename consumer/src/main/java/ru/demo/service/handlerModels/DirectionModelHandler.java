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
import ru.demo.models.DirectionsModelResponseFromHr;
import ru.demo.models.DivisionsModelResponseFromHr;
import ru.demo.requestSenders.CreaterRequestToWSS;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.util.HashMap;

@Service
public class DirectionModelHandler {
    private static final Logger log = LoggerFactory.getLogger(CompanyModelHandler.class);
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final CreaterRequestToWSS createrRequestToWSS;
    private final XMLParser xmlParser;
    private final IncomingDataHandler dataHandler;

    @Autowired
    public DirectionModelHandler(SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, CreaterRequestToWSS createrRequestToWSS, XMLParser xmlParser, IncomingDataHandler dataHandler) {
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.createrRequestToWSS = createrRequestToWSS;
        this.xmlParser = xmlParser;
        this.dataHandler = dataHandler;
    }

    public void handleDirectionsModel(JSONArray dataArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            DirectionsModelResponseFromHr directionsModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), DirectionsModelResponseFromHr.class);
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(453, "Код", directionsModel.getId());
            String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
            log.info("responseFromWSS: " + responseFromWSS);

            SOAPMessage soapMessageToSend;
            if (responseFromWSS.contains("<Fields>")) {
                soapMessageToSend = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(453, xmlParser.returnItemIdFromWSS(responseFromWSS), handleModel(directionsModel));
            } else {
                soapMessageToSend = soapMessageFactoryCreateObject.createSoapRequest(453, handleModel(directionsModel));
            }

            log.info("responseFromWSSCreateOrUpdate: " + createrRequestToWSS.sendSoapRequest(soapMessageToSend));
        } catch (Exception e) {
            log.error("Error while handling DirectionsModel: " + e.getMessage(), e);
        }
    }

    private HashMap<String, String> handleModel(DirectionsModelResponseFromHr directionsModel) throws Exception {
        HashMap<String, String> hashValueMapForDirection = new HashMap<>();
        hashValueMapForDirection.put("Название", directionsModel.getName());
        hashValueMapForDirection.put("Префикс", directionsModel.getPrefix());
        hashValueMapForDirection.put("Дивизион", searchNameDirectionsInWSS(directionsModel));
        hashValueMapForDirection.put("Код", String.valueOf(directionsModel.getId()));
        hashValueMapForDirection.put("Не актуален", String.valueOf(booleanActualFromHR(directionsModel.getIsNotActive())));

        return hashValueMapForDirection;
    }

    private String searchNameDirectionsInWSS(DirectionsModelResponseFromHr directionsModel) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject(dataHandler.requestToHR("isDivision", directionsModel.getParent_id()));
        JSONArray dataArray = jsonObject.getJSONArray("data");
        DivisionsModelResponseFromHr divisionsModel = new ObjectMapper().readValue(dataArray.getJSONObject(0).toString(), DivisionsModelResponseFromHr.class);
        try {
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(452, "Код", divisionsModel.getId());
            return String.valueOf(xmlParser.returnItemIdFromWSS(createrRequestToWSS.sendSoapRequest(soapMessageFindObjects)));
        } catch (Exception e) {
            log.info("Error in searchNameDirectionsInWSS in request to WSS for divisionId");
            return "Error in searchNameDirectionsInWSS in request to WSS for divisionId";
        }
    }

    private Boolean booleanActualFromHR(String isNotActive) {
        return !isNotActive.contains("0");
    }
}
