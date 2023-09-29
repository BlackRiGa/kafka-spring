package ru.demo.service.handlerModels;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.demo.parsers.XMLParser;
import ru.demo.models.CompanyModelResponseFromHr;
import ru.demo.requestSenders.CreaterRequestToWSS;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryCreateObject;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryFindObjects;
import ru.demo.service.soapMessageFactory.SoapMessageFactoryUpdateObject;

import javax.xml.soap.SOAPMessage;
import java.util.HashMap;

@Service
public class CompanyModelHandler {
    private static final Logger log = LoggerFactory.getLogger(CompanyModelHandler.class);
    private final SoapMessageFactoryFindObjects soapMessageFactoryFindObjects;
    private final SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject;
    private final SoapMessageFactoryCreateObject soapMessageFactoryCreateObject;
    private final CreaterRequestToWSS createrRequestToWSS;
    private final XMLParser xmlParser;

    @Autowired
    public CompanyModelHandler(SoapMessageFactoryFindObjects soapMessageFactoryFindObjects, SoapMessageFactoryUpdateObject soapMessageFactoryUpdateObject, SoapMessageFactoryCreateObject soapMessageFactoryCreateObject, CreaterRequestToWSS createrRequestToWSS, XMLParser xmlParser) {
        this.soapMessageFactoryFindObjects = soapMessageFactoryFindObjects;
        this.soapMessageFactoryUpdateObject = soapMessageFactoryUpdateObject;
        this.soapMessageFactoryCreateObject = soapMessageFactoryCreateObject;
        this.createrRequestToWSS = createrRequestToWSS;
        this.xmlParser = xmlParser;
    }

    public void handleCompanyModel(JSONArray dataArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CompanyModelResponseFromHr companyModel = objectMapper.readValue(dataArray.getJSONObject(0).toString(), CompanyModelResponseFromHr.class);
            SOAPMessage soapMessageFindObjects = soapMessageFactoryFindObjects.createSoapRequestFindObjects(105, "Код", companyModel.getId());
            String responseFromWSS = createrRequestToWSS.sendSoapRequest(soapMessageFindObjects);
            log.info("responseFromWSS: " + responseFromWSS);

            SOAPMessage soapMessageToSend;
            if (responseFromWSS.contains("<Fields>")) {
                soapMessageToSend = soapMessageFactoryUpdateObject.createUpdateObjectSoapRequest(105, xmlParser.returnItemIdFromWSS(responseFromWSS), handleModel(companyModel));
            } else {
                soapMessageToSend = soapMessageFactoryCreateObject.createSoapRequest(105, handleModel(companyModel));
            }

            log.info("responseFromWSSCreateOrUpdate: " + createrRequestToWSS.sendSoapRequest(soapMessageToSend));
        } catch (Exception e) {
            log.error("Error while handling CompanyModel: " + e.getMessage(), e);
        }
    }

    public HashMap<String, String> handleModel(CompanyModelResponseFromHr model) {
        HashMap<String, String> hashValueMap = new HashMap<>();
        hashValueMap.put("Название", model.getName());
        hashValueMap.put("Префикс", model.getPrefix());
        hashValueMap.put("Код МДМ", String.valueOf(model.getOrgCodeMdm()));
        hashValueMap.put("Код", String.valueOf(model.getId()));

        return hashValueMap;
    }
}
