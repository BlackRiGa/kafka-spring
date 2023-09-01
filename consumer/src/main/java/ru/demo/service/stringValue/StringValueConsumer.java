package ru.demo.service.stringValue;

import java.util.List;
import ru.demo.model.StringValue;

public interface StringValueConsumer {

    void accept(List<StringValue> value);
}
