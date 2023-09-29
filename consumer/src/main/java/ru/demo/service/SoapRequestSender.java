package ru.demo.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.demo.requestSenders.IncomingDataHandler;
import ru.demo.parsers.JsonParser;
import ru.demo.service.handlerModels.CompanyModelHandler;
import ru.demo.service.handlerModels.DirectionModelHandler;
import ru.demo.service.handlerModels.DivisionModelHandler;
import ru.demo.service.handlerModels.UnitModelHandler;

@Service
public class SoapRequestSender {
    private static final Logger log = LoggerFactory.getLogger(SoapRequestSender.class);
    private final CompanyModelHandler companyModelHandler;
    private final DivisionModelHandler divisionModelHandler;
    private final DirectionModelHandler directionModelHandler;
    private final UnitModelHandler unitModelHandler;
    private final IncomingDataHandler dataHandler;
    private final JsonParser jsonParser;

    @Autowired
    public SoapRequestSender(CompanyModelHandler companyModelHandler, IncomingDataHandler dataHandler, JsonParser jsonParser, DivisionModelHandler divisionModelHandler, DirectionModelHandler directionModelHandler, UnitModelHandler unitModelHandler) {
        this.companyModelHandler = companyModelHandler;
        this.dataHandler = dataHandler;
        this.jsonParser = jsonParser;
        this.divisionModelHandler = divisionModelHandler;
        this.directionModelHandler = directionModelHandler;
        this.unitModelHandler = unitModelHandler;
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
                    case "isCompany" -> companyModelHandler.handleCompanyModel(dataArray);
                    case "isDirection" -> directionModelHandler.handleDirectionsModel(dataArray);
                    case "isDivision" -> divisionModelHandler.handleDivisionsModel(dataArray);
                    case "isStuff" -> System.out.println("isStuff");//TODO добавление в БД
                    case "isUnit" -> unitModelHandler.handleUnitModel(dataArray);
                    default -> log.info("Ошибка в операторе switch-case в методе choiceOfMethodAction");
                }
            } else {
                log.info("Данные от HR по идентификатору " + jsonParser.returnId(value) + " отсутствуют");
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке запроса: " + e.getMessage(), e);
        }
    }
}
