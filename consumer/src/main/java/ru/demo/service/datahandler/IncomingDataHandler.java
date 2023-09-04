package ru.demo.service.datahandler;

import org.springframework.stereotype.Service;
import ru.demo.model.StringValue;

@Service
public class IncomingDataHandler {
    private String readValueFromJSON(StringValue value) {
        return "test";
    }
}
