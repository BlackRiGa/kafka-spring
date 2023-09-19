package ru.demo.service;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.demo.model.StringValue;

public class StringValueSource implements ValueSource {
    private static final Logger log = LoggerFactory.getLogger(StringValueSource.class);
    private final AtomicLong nextValue = new AtomicLong(1);
    private final DataSender valueConsumer;

    public StringValueSource(DataSender dataSender) {
        this.valueConsumer = dataSender;
    }

    @Override
    public void generate() {
        var executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> valueConsumer.send(makeValue()), 0, 1, TimeUnit.HOURS);
        log.info("generation started");
    }

    private StringValue makeValue() {
        var id = nextValue.getAndIncrement();
        return new StringValue(id, "{\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"string\",\n" +
                "      \"parent_id\": 0,\n" +
                "      \"is_entity\": 1,\n" +
                "      \"is_service\": 1,\n" +
                "      \"company_id\": 0,\n" +
                "      \"service_id\": 0,\n" +
                "      \"chief_id\": 0,\n" +
                "      \"is_deleted\": 1,\n" +
                "      \"mdm_code\": 0,\n" +
                "      \"is_special\": true,\n" +
                "      \"chief\": {\n" +
                "        \"id\": 0,\n" +
                "        \"name\": \"string\",\n" +
                "        \"login\": \"string\"\n" +
                "      }\n" +
                "    }");
    }
}
