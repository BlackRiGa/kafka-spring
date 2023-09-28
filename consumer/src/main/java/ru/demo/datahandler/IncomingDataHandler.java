package ru.demo.datahandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class IncomingDataHandler {
    private static final Logger log = LoggerFactory.getLogger(IncomingDataHandler.class);
    @Value("${spring.application.url.companies}")
    private String companiesUrl;
    @Value("${spring.application.url.unit}")
    private String unitUrl;
    @Value("${spring.application.url.staff}")
    private String staffUrl;
    @Value("${spring.application.url.directions}")
    private String directionUrl;
    @Value("${spring.application.url.division}")
    private String divisionUrl;
    @Value("${spring.application.x-api-key}")
    private String xAPIKey;
    @Value("${spring.application.url_hr}")
    private String urlHr;

    /**
     * Выбор URL для запроса в HR в зависимости от типа объекта и его ID.
     *
     * @param value Тип объекта
     * @param id    ID объекта
     * @return URL для запроса в HR
     */
    public String choiceUrlToHR(String value, Integer id) {
        String endpoint = "";

        switch (value) {
            case "isCompany" -> endpoint = companiesUrl;
            case "isDirection" -> endpoint = directionUrl;
            case "isStuff" -> endpoint = staffUrl;
            case "isDivision" -> endpoint = divisionUrl;
            case "isUnit" -> endpoint = unitUrl;
            default -> log.info("Unknown value in choiceUrlToHR");
        }

        log.info(urlHr + endpoint + "?id=" + id);
        return urlHr + endpoint + "?id=" + id;
    }

    /**
     * Отправка запроса в HR и получение ответа.
     *
     * @param value Тип объекта
     * @param id    ID объекта
     * @return Ответ от HR
     */
    public String requestToHR(String booleanAddressHr, Integer id) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(choiceUrlToHR(booleanAddressHr, id));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-api-key", xAPIKey);

            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            log.info("Response Code: " + responseCode);
            log.info("Response: " + response);

        } catch (Exception e) {
            log.error("An error occurred during the request to HR: " + e.getMessage());
        }

        return response.toString();
    }
}
