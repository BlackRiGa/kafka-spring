package ru.demo.service.datahandler;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JsonParser {
    private static final Map<String, Function<JSONObject, String>> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("isPerson", data -> "isPerson");
        TYPE_MAP.put("isCompany", data -> "isCompany");
        TYPE_MAP.put("isDivision", data -> "isDivision");
        TYPE_MAP.put("isStaff", data -> "isStaff");
        TYPE_MAP.put("isUnit", data -> "isUnit");
    }

    public String returnBooleanData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("data");

            for (Map.Entry<String, Function<JSONObject, String>> entry : TYPE_MAP.entrySet()) {
                if (data.has(entry.getKey())) {
                    return entry.getValue().apply(data);
                }
            }

            throw new JSONException("Unknown Object in returnBooleanData");
        } catch (JSONException e) {
            log.error("Error parsing JSON: " + e.getMessage());
            throw new JSONException("Unknown Object in returnBooleanData", e);
        }
    }

    public Integer returnId(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject data = jsonObject.getJSONObject("data");
        return data.getInt("id");
    }
}
